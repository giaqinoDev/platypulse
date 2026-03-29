package com.giosmdev.package_radar.repository.RawGitHubRegistryResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RawGitHubRegistryResponse {
    private WebClient webClient;

    public RawGitHubRegistryResponse(WebClient webClient){
        this.webClient = webClient;
    }
}
