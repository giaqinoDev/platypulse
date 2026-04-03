package com.giosmdev.package_radar.service.GitHubLayerApis;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.configs.APIResponseLayer.ApiStatus;
import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.ProcessingException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.GitHubRegistry.RepoCommitInfo;
import com.giosmdev.package_radar.service.ApiErrorHandling.ApiErrorHandler;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class GitHubCommitsAPI {
    private final WebClient webClient;
    private final ApiErrorHandler handleError;
    public GitHubCommitsAPI(WebClient webClient, ApiErrorHandler handleError){
        this.webClient = webClient;
        this.handleError = handleError;
    }

    public Mono<ApiStatus<RepoCommitInfo>> fetch(String group, String repo){
        System.out.println("Fetchign commit info from api");
        Mono<RepoCommitInfo> commit_info = webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                .scheme("https")
                                .host("api.github.com")
                                .path("/repos/{group}/{repo}/commits")
                                .queryParam("per_page", "100")
                                .build(group, repo)
                            )
                            .header("Authorization", "Bearer " + System.getenv("GITHUB_ACCESS_TOKEN"))
                            .header("Accept", "application/vnd.github+json")
                            .header("X-GitHub-Api-Version", "2026-03-10")
                            .retrieve()
                            .onStatus(
                                status -> status.is4xxClientError(),
                                reponse -> Mono.error(new PackageNotFoundException())
                            )
                            .onStatus(
                                status -> status.is5xxServerError(),
                                response -> {System.out.println("AAHHAHAH");
                                return Mono.error(new RegistryException());}
                            )
                            .bodyToMono(JsonNode.class)
                            .timeout(Duration.ofSeconds(7))
                            .map(response -> processCommitInfo(response));

        Mono<ApiStatus<RepoCommitInfo>> wrapper_result = handleError.handle(commit_info);
        return wrapper_result;
    } 

    private RepoCommitInfo processCommitInfo(JsonNode response){
        List<Instant> commit_timeline = new ArrayList<Instant>();
        Map<Instant, Integer> commit_timeline_volume = new HashMap<Instant, Integer>();
        long commits_last_30_days;
        long commits_last_90_days;

        Instant currDate = Instant.now().truncatedTo(ChronoUnit.DAYS);

        try{
            response.forEach(commit_log -> {
                Instant commit_date = Instant.parse(commit_log.get("commit").get("author").get("date").asString());
                Instant commit_date_truncated = commit_date.truncatedTo(ChronoUnit.DAYS);
                //populate timeline
                commit_timeline.add(commit_date);

                //populate timeline frequency
                if(commit_timeline_volume.containsKey(commit_date_truncated)){
                    int commit_amount = commit_timeline_volume.get(commit_date_truncated);
                    commit_timeline_volume.replace(commit_date_truncated, commit_amount + 1);
                }
                else{
                    commit_timeline_volume.put(commit_date_truncated, 1);
                }
            });

            commits_last_30_days = commit_timeline.stream().filter(commit -> ChronoUnit.DAYS.between(commit, currDate) <= 30).count();
            commits_last_90_days = commit_timeline.stream().filter(commit -> ChronoUnit.DAYS.between(commit, currDate) <= 90).count();
            return new RepoCommitInfo(commit_timeline, commit_timeline_volume, commits_last_30_days, commits_last_90_days);
        }
        catch(Exception error){
            System.out.println("Some Error on commti info level");
            throw new ProcessingException(error.getStackTrace());
        }
    }
}
