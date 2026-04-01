package com.giosmdev.package_radar.service.GitHubLayerApis;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.GitHubRegistry.PackageRepoInfo;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;


@Service
public class GitHubRegistryAPI {
    private final WebClient webClient;
    //private final String BASE_GITHUB_URL = "https://api.github.com/repos";

    public GitHubRegistryAPI(WebClient webClient){
        this.webClient = webClient;
    }
    
    public Mono<PackageRepoInfo> fetch(String group, String repo){
        Mono<PackageRepoInfo> gitHubPAI_res = webClient.get()
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
                            response -> Mono.error(new PackageNotFoundException())
                        )
                        .onStatus(
                            status -> status.is5xxServerError(),
                            response -> Mono.error(new RegistryException())
                        )
                        .bodyToMono(JsonNode.class)
                        .timeout(Duration.ofSeconds(7))
                        .map(response -> {
                            int stars_count = response.get("stargazers_count").asInt();
                            int forks_count = response.get("forks_count").asInt();
                            int open_issues_count = response.get("open_issues_count").asInt();
                            boolean isArchived = response.get("archived").asBoolean();

                            Instant pushed_at = Instant.parse(response.get("pushed_at").asString());
                            Instant created_at = Instant.parse(response.get("created_at").asString());
                            Instant updated_at = Instant.parse(response.get("updated_at").asString());

                            Instant curr_date = Instant.now();
                            long repo_age = ChronoUnit.DAYS.between(created_at, curr_date);
                            long days_since_last_commit = ChronoUnit.DAYS.between(pushed_at, curr_date);
                            
                            return new PackageRepoInfo(stars_count, forks_count, open_issues_count, isArchived, pushed_at, created_at, updated_at, repo_age, days_since_last_commit);

                        });
        return gitHubPAI_res;
    }
}
