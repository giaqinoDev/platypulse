package com.giosmdev.package_radar.repository.RawGitHubRegistryResponse;

import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.configs.APIResponseLayer.ApiStatus;
import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.GitHubRegistry.PackageRepoInfo;
import com.giosmdev.package_radar.model.GitHubRegistry.RawGitHubResponse;
import com.giosmdev.package_radar.model.GitHubRegistry.RepoCommitInfo;
import com.giosmdev.package_radar.service.GitHubLayerApis.GitHubCommitsAPI;
import com.giosmdev.package_radar.service.GitHubLayerApis.GitHubRepoAPI;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class RawGitHubRegistryResponse {
    private final GitHubRepoAPI gitHubRegistryAPI;
    private final GitHubCommitsAPI gitHubCommitsAPI;

    public RawGitHubRegistryResponse(GitHubRepoAPI gitHubRegistryAPI, GitHubCommitsAPI gitHubCommitsAPI){
        this.gitHubRegistryAPI = gitHubRegistryAPI;
        this.gitHubCommitsAPI = gitHubCommitsAPI;
    }

    public Mono<ResponseEntity<RawGitHubResponse>> genRawGitHubResponse(String group, String repo){
        Mono<PackageRepoInfo> repo_response = gitHubRegistryAPI.fetch("greatSumini", "react-facebook-login");

        Mono<ApiStatus<RepoCommitInfo>> commits_info_response = gitHubCommitsAPI.fetch("greatSumini", "react-facebook-login");
        System.out.println("raw git");
        return repo_response.flatMap(response -> {
            System.out.println("flating");
            return Mono.zip(commits_info_response, Mono.just(new RepoCommitInfo()))
                                .map(tuple -> {
                                    RepoCommitInfo commit_info = tuple.getT1().getData();

                                    return ResponseEntity.ok(new RawGitHubResponse(response, commit_info));
                                });
                            }
        )
        .onErrorResume(PackageNotFoundException.class, error ->
            Mono.just(ResponseEntity.status(404).build())
         )
        .onErrorResume(RegistryException.class, error ->
            Mono.just(ResponseEntity.status(503).build())
        )
        .onErrorResume(TimeoutException.class, error ->
            Mono.just(ResponseEntity.status(504).build())
        )
        .onErrorResume(error ->
            Mono.just(ResponseEntity.status(500).build())
        );
    }
}
