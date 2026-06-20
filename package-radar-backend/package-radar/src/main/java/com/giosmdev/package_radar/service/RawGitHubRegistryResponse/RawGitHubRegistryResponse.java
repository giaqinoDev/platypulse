package com.giosmdev.package_radar.service.RawGitHubRegistryResponse;

import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.configs.APIResponseLayer.ApiStatus;
import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.GitHubRegistry.PackageRepoInfo;
import com.giosmdev.package_radar.model.GitHubRegistry.RawGitHubResponse;
import com.giosmdev.package_radar.model.GitHubRegistry.RepoCommitInfo;
import com.giosmdev.package_radar.model.GitHubRegistry.RepoContributorsInfo;
import com.giosmdev.package_radar.service.ApiLayer.GitHubLayerApis.GitHubCommitsAPI;
import com.giosmdev.package_radar.service.ApiLayer.GitHubLayerApis.GitHubRepoAPI;
import com.giosmdev.package_radar.service.ApiLayer.GitHubLayerApis.GitHubRepoContributorsAPI;

import reactor.core.publisher.Mono;

@Service
public class RawGitHubRegistryResponse {
    private final GitHubRepoAPI gitHubRegistryAPI;
    private final GitHubCommitsAPI gitHubCommitsAPI;
    private final GitHubRepoContributorsAPI gitHubRepoContributorsAPI;

    public RawGitHubRegistryResponse(GitHubRepoAPI gitHubRegistryAPI, GitHubCommitsAPI gitHubCommitsAPI, GitHubRepoContributorsAPI gitHubRepoContributorsAPI){
        this.gitHubRegistryAPI = gitHubRegistryAPI;
        this.gitHubCommitsAPI = gitHubCommitsAPI;
        this.gitHubRepoContributorsAPI = gitHubRepoContributorsAPI;
    }

    public Mono<ResponseEntity<RawGitHubResponse>> genRawGitHubResponse(String group, String repo){
        Mono<PackageRepoInfo> repo_response = gitHubRegistryAPI.fetch(group, repo);

        Mono<ApiStatus<RepoCommitInfo>> commits_info_response = gitHubCommitsAPI.fetch(group, repo);

        Mono<ApiStatus<RepoContributorsInfo>> contributors_info_response = gitHubRepoContributorsAPI.fetch(group, repo);
        

        return repo_response.flatMap(response -> {
            System.out.println("VERYFYING");
            return Mono.zip(commits_info_response, contributors_info_response).map(tuple -> {
                    System.out.println("Zipping Monos");
                    RepoCommitInfo commit_info = tuple.getT1().getData();
                    RepoContributorsInfo contributor_info = tuple.getT2().getData();
                    return ResponseEntity.ok(new RawGitHubResponse(response, commit_info, contributor_info));
                });
            })
        .onErrorResume(PackageNotFoundException.class, error ->{
                System.out.println("REPO DOES NOT EXIST: Graceful return null");
                return Mono.just(ResponseEntity.status(200).body(null)); //Force Null: new RawGitHubResponse(null, null, null)
            }
        )
        .onErrorResume(RegistryException.class, error ->
            Mono.just(ResponseEntity.status(503).build())
        )
        .onErrorResume(TimeoutException.class, error ->
            Mono.just(ResponseEntity.status(504).build())
        )
        .onErrorResume(error ->
            {System.out.println("Error");
            return Mono.just(ResponseEntity.status(500).build());}
        );
    }
}
