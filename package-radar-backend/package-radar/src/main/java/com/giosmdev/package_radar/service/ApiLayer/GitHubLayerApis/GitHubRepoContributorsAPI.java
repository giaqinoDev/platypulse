package com.giosmdev.package_radar.service.ApiLayer.GitHubLayerApis;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.dto.FallbackFetch;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiNotFoundException;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiRateLimitException;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiUnavailableException;
import com.giosmdev.package_radar.model.GitHubRegistry.RepoContributorsInfo;
import com.giosmdev.package_radar.service.ApiLayer.ApiErrorHandling.ApiErrorInterceptor;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class GitHubRepoContributorsAPI {
    private final WebClient webClient;
    private final ApiErrorInterceptor apiErrorInterceptor;

    public GitHubRepoContributorsAPI(WebClient webClient, ApiErrorInterceptor apiErrorInterceptor){
        this.webClient = webClient;
        this.apiErrorInterceptor = apiErrorInterceptor;
    }

    public Mono<FallbackFetch<RepoContributorsInfo>> fetch(String group, String repo){
        System.out.println("Fetching GItHub Repo contributors info from api");

        String route = "https/api.github.com/repos/" + group +"/" + repo + "/contributors";
        Mono<RepoContributorsInfo> contributors_info = webClient.get()
                                    .uri(uriBuilder -> uriBuilder
                                        .scheme("https")
                                        .host("api.github.com")
                                        .path("/repos/{group}/{repo}/contributors")
                                        .queryParam("per_page", "100")
                                        .build(group, repo)
                                    )
                                    .header("Authorization", "Bearer " + System.getenv("GITHUB_ACCESS_TOKEN"))
                                    .header("Accept", "application/vnd.github+json")
                                    .header("X-GitHub-Api-Version", "2026-03-10")
                                    .retrieve()
                                    .onStatus(
                                            status -> status.value() == 404,
                                            response -> Mono.error(new ExternalApiNotFoundException("Repo not found in GitHub registry", route))
                                    )
                                    .onStatus(
                                        status -> status.value() == 429,
                                        response -> Mono.error(new ExternalApiRateLimitException("GitHub registry rate limit exceeded", route))
                                    )
                                    .onStatus(
                                        status -> status.is5xxServerError(),
                                        response -> {
                                            System.out.println("500 Level Error");
                                            return Mono.error(new ExternalApiUnavailableException("GitHub Registry unavailable", route));
                                        }
                                    )
                                    .bodyToMono(JsonNode.class)
                                    .timeout(Duration.ofSeconds(10))
                                    .map(response -> proccessResponse(response, null)); //header_payload.getHeaders()
        Mono<FallbackFetch<RepoContributorsInfo>> wrapper_result = apiErrorInterceptor.handle(contributors_info);
        return wrapper_result;
    }

    private RepoContributorsInfo proccessResponse(JsonNode contributors, HttpHeaders header){
        if(contributors == null || contributors.isEmpty()){
            return new RepoContributorsInfo(0, 0, 0, 0);
        }
        System.out.println("processing Contirbutors");
        AtomicInteger total_commits = new AtomicInteger();
        int top_contributor_commits = contributors.get(0).get("contributions").asInt();
        double top_commits_ratio;

        contributors.forEach(contributor -> {
            String contributor_name = contributor.get("login").asString();
            String contributor_type = contributor.get("type").asString();
            int contributor_commit_amount = contributor.get("contributions").asInt();

            if(contributor_name.contains("[bot]") || contributor_type.equals("Bot")){
                return;
            }
            int old_amount = total_commits.get();
            total_commits.set(contributor_commit_amount + old_amount);
        });

        top_commits_ratio = top_contributor_commits / (double) total_commits.get();
        int contributors_count = contributors.size(); // filtered out bots from contributors list
        return new RepoContributorsInfo(total_commits.get(), contributors_count, top_contributor_commits, top_commits_ratio);
    }
}
