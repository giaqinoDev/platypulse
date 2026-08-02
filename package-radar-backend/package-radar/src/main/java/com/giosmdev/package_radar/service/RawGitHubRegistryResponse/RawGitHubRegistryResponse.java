package com.giosmdev.package_radar.service.RawGitHubRegistryResponse;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.dto.FallbackFetch;
import com.giosmdev.package_radar.model.GitHubRegistry.PackageRepoInfo;
import com.giosmdev.package_radar.model.GitHubRegistry.RawGitHubResponse;
import com.giosmdev.package_radar.model.GitHubRegistry.RepoCommitInfo;
import com.giosmdev.package_radar.model.GitHubRegistry.RepoContributorsInfo;
import com.giosmdev.package_radar.service.ApiLayer.ApiErrorHandling.ApiErrorInterceptor;
import com.giosmdev.package_radar.service.ApiLayer.GitHubLayerApis.GitHubCommitsAPI;
import com.giosmdev.package_radar.service.ApiLayer.GitHubLayerApis.GitHubRepoAPI;
import com.giosmdev.package_radar.service.ApiLayer.GitHubLayerApis.GitHubRepoContributorsAPI;

import reactor.core.publisher.Mono;

@Service
public class RawGitHubRegistryResponse {
    private final GitHubRepoAPI gitHubRepoAPI;
    private final GitHubCommitsAPI gitHubCommitsAPI;
    private final GitHubRepoContributorsAPI gitHubRepoContributorsAPI;
    private final ApiErrorInterceptor apiErrorInterceptor;

    public RawGitHubRegistryResponse(
        GitHubRepoAPI gitHubRepoAPI, 
        GitHubCommitsAPI gitHubCommitsAPI, 
        GitHubRepoContributorsAPI gitHubRepoContributorsAPI,
        ApiErrorInterceptor apiErrorInterceptor
    ){
        this.gitHubRepoAPI = gitHubRepoAPI;
        this.gitHubCommitsAPI = gitHubCommitsAPI;
        this.gitHubRepoContributorsAPI = gitHubRepoContributorsAPI;
        this.apiErrorInterceptor = apiErrorInterceptor;
    }

    public Mono<FallbackFetch<RawGitHubResponse>> genRawGitHubResponse(String group, String repo){
        Mono<PackageRepoInfo> repo_response = gitHubRepoAPI.fetch(group, repo);

        Mono<FallbackFetch<RepoCommitInfo>> commits_info_response = gitHubCommitsAPI.fetch(group, repo);

        Mono<FallbackFetch<RepoContributorsInfo>> contributors_info_response = gitHubRepoContributorsAPI.fetch(group, repo);
        
        Mono<RawGitHubResponse> git_repo = repo_response.flatMap(response -> {
            return Mono.zip(commits_info_response, contributors_info_response).map(tuple -> {
                    System.out.println("Zipping Monos");
                    RepoCommitInfo commit_info = tuple.getT1().getData();
                    RepoContributorsInfo contributor_info = tuple.getT2().getData();
                    return new RawGitHubResponse(response, commit_info, contributor_info);
                });
            });

        Mono<FallbackFetch<RawGitHubResponse>> condensed_response = apiErrorInterceptor.handle(git_repo);
        return condensed_response;
        // .onErrorResume(PackageNotFoundException.class, error ->{
        //         System.out.println("REPO DOES NOT EXIST: Graceful return null");
        //         return Mono.just(ResponseEntity.status(200).body(null)); //Force Null: new RawGitHubResponse(null, null, null)
        //     }
        // )
        // .onErrorResume(RegistryException.class, error ->
        //     Mono.just(ResponseEntity.status(503).build())
        // )
        // .onErrorResume(TimeoutException.class, error ->
        //     Mono.just(ResponseEntity.status(504).build())
        // )
        // .onErrorResume(error ->
        //     {System.out.println("Error");
        //     return Mono.just(ResponseEntity.status(500).build());}
        // );
    }
}
