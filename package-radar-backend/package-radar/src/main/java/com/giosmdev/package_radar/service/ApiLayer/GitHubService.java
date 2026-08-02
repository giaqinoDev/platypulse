package com.giosmdev.package_radar.service.ApiLayer;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.dto.FallbackFetch;
import com.giosmdev.package_radar.model.GitHubRegistry.RawGitHubResponse;
import com.giosmdev.package_radar.service.RawGitHubRegistryResponse.RawGitHubRegistryResponse;

import reactor.core.publisher.Mono;

@Service
public class GitHubService {
    private RawGitHubRegistryResponse rawGitHubResponse;
    public GitHubService(RawGitHubRegistryResponse rawGitHubResponse){
        this.rawGitHubResponse = rawGitHubResponse;
    }
    
    public Mono<FallbackFetch<RawGitHubResponse>> getGitHubResponse(String group, String repo){
        if(group == null || repo == null){
            return Mono.just(new FallbackFetch<RawGitHubResponse>(null, null));
        }
        return rawGitHubResponse.genRawGitHubResponse(group, repo);
    }
}
