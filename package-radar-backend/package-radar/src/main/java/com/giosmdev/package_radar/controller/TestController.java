package com.giosmdev.package_radar.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.giosmdev.package_radar.model.GitHubRegistry.PackageRepoInfo;
import com.giosmdev.package_radar.service.GitHubService;

import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/test")
public class TestController {
    private final GitHubService gitHubService;

    public TestController(GitHubService gitHubService){
        this.gitHubService = gitHubService;
    }
    @GetMapping("github-reg-test")
    public Mono<PackageRepoInfo> getGitHubRes() {
        return gitHubService.getGitHubResponse("blah", "blah");
    }
    
}
