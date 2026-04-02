package com.giosmdev.package_radar.service.GitHubLayerApis;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GitHubCommitsAPI {
    private final WebClient webClient;

    public GitHubCommitsAPI(WebClient webClient){
        this.webClient = webClient;
    }

    public Mono<T> fetch(){
        
    } 
}
