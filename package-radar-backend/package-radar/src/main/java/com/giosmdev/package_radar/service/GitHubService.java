package com.giosmdev.package_radar.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.model.GitHubRegistry.RawGitHubResponse;
import com.giosmdev.package_radar.repository.RawGitHubRegistryResponse.RawGitHubRegistryResponse;

import reactor.core.publisher.Mono;

@Service
public class GitHubService {
    private RawGitHubRegistryResponse rawGitHubResponse;
    public GitHubService(RawGitHubRegistryResponse rawGitHubResponse){
        this.rawGitHubResponse = rawGitHubResponse;
    }
    
    public Mono<ResponseEntity<RawGitHubResponse>> getGitHubResponse(String group, String repo){
        return rawGitHubResponse.genRawGitHubResponse(group, repo);
    }
}
