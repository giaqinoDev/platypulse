package com.giosmdev.package_radar.service.GitHubLayerApis;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.ProcessingException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.GitHubRegistry.PackageRepoInfo;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;


@Service
public class GitHubRepoAPI {
    private final WebClient webClient;
    //private final String BASE_GITHUB_URL = "https://api.github.com/repos";

    public GitHubRepoAPI(WebClient webClient){
        this.webClient = webClient;
    }
    
    public Mono<PackageRepoInfo> fetch(String group, String repo){
        System.out.println("Github repo api call");
        //Priority API Call
        Mono<PackageRepoInfo> gitHubAPI_res = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.github.com")
                            .path("/repos/{group}/{repo}")
                            .build(group, repo)
                        )
                        .header("Authorization", "Bearer " + System.getenv("GITHUB_ACCESS_TOKEN"))
                        .header("Accept", "application/vnd.github+json")
                        .header("X-GitHub-Api-Version", "2026-03-10")
                        .retrieve()
                        .onStatus(
                            status -> status.is4xxClientError(),
                            response -> {
                                System.out.println("GttHub Repo 404 Error");
                                return Mono.error(new PackageNotFoundException());
                            }
                        )
                        .onStatus(
                            status -> status.is5xxServerError(),
                            response -> Mono.error(new RegistryException())
                        )
                        .bodyToMono(JsonNode.class)
                        .timeout(Duration.ofSeconds(7))
                        .map(response -> {
                            try{
                                int stars_count = response.get("stargazers_count").asInt();
                                int forks_count = response.get("forks_count").asInt();
                                boolean isArchived = response.get("archived").asBoolean();

                                Instant pushed_at = Instant.parse(response.get("pushed_at").asString());
                                Instant created_at = Instant.parse(response.get("created_at").asString());
                                Instant updated_at = Instant.parse(response.get("updated_at").asString());

                                Instant curr_date = Instant.now();
                                long repo_age = ChronoUnit.DAYS.between(created_at, curr_date);
                                long days_since_last_push = ChronoUnit.DAYS.between(pushed_at, curr_date);
                                
                                return new PackageRepoInfo(stars_count, forks_count, isArchived, pushed_at, created_at, updated_at, repo_age, days_since_last_push);
                            }
                            catch(Exception error){
                                throw new ProcessingException(error.getStackTrace());
                            }
                        });
        return gitHubAPI_res;
    }
}
