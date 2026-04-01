package com.giosmdev.package_radar.repository.RawGitHubRegistryResponse;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.model.GitHubRegistry.PackageRepoInfo;
import com.giosmdev.package_radar.service.GitHubLayerApis.GitHubRegistryAPI;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class RawGitHubRegistryResponse {
    private GitHubRegistryAPI gitHubRegistryAPI;

    public RawGitHubRegistryResponse(GitHubRegistryAPI gitHubRegistryAPI){
        this.gitHubRegistryAPI = gitHubRegistryAPI;
    }

    public Mono<PackageRepoInfo> genRawGitHubResponse(String group, String repo){
        return gitHubRegistryAPI.fetch("greatSumini", "react-facebook-login");
    }
}
