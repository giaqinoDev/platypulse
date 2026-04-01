package com.giosmdev.package_radar.service;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.repository.RawGitHubRegistryResponse.RawGitHubRegistryResponse;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class GitHubService {
    private RawGitHubRegistryResponse rawGitHubResponse;
    public GitHubService(RawGitHubRegistryResponse rawGitHubResponse){
        this.rawGitHubResponse = rawGitHubResponse;
    }
    public Mono<JsonNode> getGitHubResponse(String group, String repo){
        return rawGitHubResponse.genRawGitHubResponse(group, repo);
    }
}
