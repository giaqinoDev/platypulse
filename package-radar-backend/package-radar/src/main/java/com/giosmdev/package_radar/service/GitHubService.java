package com.giosmdev.package_radar.service;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.service.GitHubLayerApis.GitHubRegistryAPI;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class GitHubService {
    private final GitHubRegistryAPI gitHubRegistryAPI;

    public GitHubService(GitHubRegistryAPI gitHubRegistryAPI){
        this.gitHubRegistryAPI = gitHubRegistryAPI;
    }
    public Mono<JsonNode> getGitHubResponse(String group, String repo){
        return gitHubRegistryAPI.fetch("greatSumini", "react-facebook-login");
    }
}
