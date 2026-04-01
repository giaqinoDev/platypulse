package com.giosmdev.package_radar.service.GitHubLayerApis;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;


@Service
public class GitHubRegistryAPI {
    private final WebClient webClient;
    //private final String BASE_GITHUB_URL = "https://api.github.com/repos";

    public GitHubRegistryAPI(WebClient webClient){
        this.webClient = webClient;
    }
    
    public Mono<JsonNode> fetch(String group, String repo){
        Mono<JsonNode> gitHubPAI_res = webClient.get()
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
                        .timeout(Duration.ofSeconds(7));
        return gitHubPAI_res;
    }
}
