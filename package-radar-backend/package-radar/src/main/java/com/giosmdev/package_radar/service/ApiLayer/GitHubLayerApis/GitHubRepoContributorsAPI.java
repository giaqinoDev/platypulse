package com.giosmdev.package_radar.service.ApiLayer.GitHubLayerApis;

import com.giosmdev.package_radar.service.ApiLayer.DependencyTree.GenDependenciesTree;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.configs.APIResponseLayer.ApiStatus;
import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.ProcessingException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.GitHubRegistry.RepoContributorsInfo;
import com.giosmdev.package_radar.service.ApiLayer.ApiErrorHandling.ApiErrorHandler;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class GitHubRepoContributorsAPI {
    private final WebClient webClient;
    private final ApiErrorHandler errorHandler;

    public GitHubRepoContributorsAPI(WebClient webClient, ApiErrorHandler errorHandler){
        this.webClient = webClient;
        this.errorHandler = errorHandler;
    }

    public Mono<ApiStatus<RepoContributorsInfo>> fetch(String group, String repo){
        System.out.println("Fetching GItHub Repo contributors info from api");
        ResponseEntity<String> header_payload = webClient.get()
                                    .uri(uriBuilder -> uriBuilder
                                        .scheme("https")
                                        .host("api.github.com")
                                        .path("/repos/{group}/{repo}/contributors")
                                        .queryParam("per_page", "1")
                                        .queryParam("anon", "true") //include anonymous contributors
                                        .build(group, repo)
                                    )
                                    .header("Authorization", "Bearer " + System.getenv("GITHUB_ACCESS_TOKEN"))
                                    .header("Accept", "application/vnd.github+json")
                                    .header("X-GitHub-Api-Version", "2026-03-10")
                                    .retrieve()
                                    .onStatus(
                                        status -> status.is4xxClientError(),
                                        response -> {
                                            System.out.println("GItHub Contributors 404 error"); // <--------------- THIS IS BEING HIT!!!!!
                                            return Mono.error(new PackageNotFoundException());
                                        }
                                    )
                                    .onStatus(
                                        status -> status.is5xxServerError(),
                                        response -> {
                                            System.out.println("500 level github api contributors endpoint");
                                            return Mono.error(new RegistryException());
                                        }
                                    )
                                    .toEntity(String.class)
                                    .block();

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
                                        status -> status.is4xxClientError(),
                                        response -> {
                                            System.out.println("GItHub Contributors 404 error");
                                            return Mono.error(new PackageNotFoundException());
                                        }
                                    )
                                    .onStatus(
                                        status -> status.is5xxServerError(),
                                        response -> {
                                            System.out.println("500 level github api contributors endpoint");
                                            return Mono.error(new RegistryException());
                                        }
                                    )
                                    .bodyToMono(JsonNode.class)
                                    .timeout(Duration.ofSeconds(7))
                                    .map(response -> proccessResponse(response, header_payload.getHeaders()));
        Mono<ApiStatus<RepoContributorsInfo>> wrapper_result = errorHandler.handle(contributors_info);
        return wrapper_result;
    }

    private RepoContributorsInfo proccessResponse(JsonNode contributors, HttpHeaders header){
        if(contributors == null || contributors.isEmpty()){
            return new RepoContributorsInfo(0, 0, 0, 0);
        }
        System.out.println(contributors);
        System.out.println("processing Contirbutors");
        AtomicInteger total_commits = new AtomicInteger();
        int top_contributor_commits = contributors.get(0).get("contributions").asInt();
        double top_commits_ratio;

        //int contributors_count = findContributorCount(header);

        try{
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
        catch(Exception error){
            error.printStackTrace();
            throw new ProcessingException(error.getStackTrace());
        }
    }

    //Deprecated Function----------------------------------------------
    private int findContributorCount(HttpHeaders header){
        String header_link = header.getFirst("Link"); //Only works when pagination is implemented (i.e for multiple lists of contributors)
        // ^ above returns null if a header link explicitly defining pagenation does not exists
        //Requried a pagination check
        if(header_link == null){
            return 1;
        }
        
        Pattern header_pattern = Pattern.compile("page=(\\d+)>; rel=\"last\"");
        Matcher header_matcher = header_pattern.matcher(header_link);

        if(header_matcher.find()){
            return Integer.parseInt(header_matcher.group(1)) + 1;
        }
        return 1;
    }
}
