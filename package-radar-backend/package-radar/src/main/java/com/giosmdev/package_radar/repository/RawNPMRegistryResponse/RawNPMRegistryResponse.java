package com.giosmdev.package_radar.repository.RawNPMRegistryResponse;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.configs.APIResponseLayer.ApiStatus;
import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.GitHubRegistry.RawGitHubResponse;
import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.model.NPMRegistry.PackageInfo;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmResponse;
import com.giosmdev.package_radar.model.NPMRegistry.DownloadsTimeline.WeeklyDownloads;
import com.giosmdev.package_radar.service.GitHubService;
import com.giosmdev.package_radar.service.NPMLayerApis.LibrariesIoAPI;
import com.giosmdev.package_radar.service.NPMLayerApis.NPMRegistryAPI;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class RawNPMRegistryResponse {

    //API Calls
    private final NPMRegistryAPI npmRegistryAPI;
    private final LibrariesIoAPI librariesIOResponse;
    private final GitHubService gitHubService;

    public RawNPMRegistryResponse(NPMRegistryAPI npmRegistryAPI, LibrariesIoAPI librariesIOResponse, GitHubService gitHubService){
        this.npmRegistryAPI = npmRegistryAPI;
        this.librariesIOResponse = librariesIOResponse;
        this.gitHubService = gitHubService;
    }

    public Mono<ResponseEntity<RawNpmResponse>> genRawNPMResponse(String pkgName){
        //Call NPM registry call
        Mono<PackageInfo> npm_reg_response = npmRegistryAPI.getPackageInfo(pkgName);

        Mono<ApiStatus<List<WeeklyDownloads>>> npm_reg_dwnld_timeline = npmRegistryAPI.getDownloadsTimeline(pkgName);
        
        //Libraries 
        Mono<ApiStatus<LibrariesIOResponse>> libIO_response = librariesIOResponse.fetch("npm", pkgName);

        // Merge downloads timeline into response
        // if any of the arguements in Mono.zip are Mono.empty() then Mono.zip will return empty
        Mono<ResponseEntity<RawNpmResponse>> NPMResponse = npm_reg_response.flatMap(package_info -> {
            System.out.println("DO IT!");
            String group = null;
            String repo = null;
            try{
                JsonNode repo_node = package_info.getLatest_version_info().getRepository();
                if(repo_node != null){
                    String repo_url = repo_node.path("url").asString(null);
                    String[] segments = repo_url.split("/");
                    group = segments[segments.length - 2];
                    repo = segments[segments.length - 1].substring(0, segments[segments.length - 1].indexOf("."));
                }
            }
            catch(Exception error){
                error.printStackTrace();
            }
            //System.out.println(group + "/" + repo);
            Mono<ResponseEntity<RawGitHubResponse>> gitHubApiResponse = gitHubService.getGitHubResponse(group, repo);
            return Mono.zip(npm_reg_dwnld_timeline, libIO_response, gitHubApiResponse)
                    .map(tuple -> {
                        RawGitHubResponse gitHub_response = tuple.getT3().getBody();
                        System.out.println("github api repsonse status: " + tuple.getT3().getStatusCode());

                        List<WeeklyDownloads> downloads_timeline = tuple.getT1().getData();
                        System.out.println("downloads_timeline: " + tuple.getT1().getStatus());

                        LibrariesIOResponse libIO_Response = tuple.getT2().getData();
                        System.out.println("libIO_Response: " + tuple.getT2().getStatus());

                        return ResponseEntity.ok(new RawNpmResponse(package_info, downloads_timeline, libIO_Response, gitHub_response));
                    });
            }
        )
        .onErrorResume(PackageNotFoundException.class, error ->
                            Mono.just(ResponseEntity.status(404).build())
                        )
                        .onErrorResume(RegistryException.class, error ->
                            Mono.just(ResponseEntity.status(503).build())
                        )
                        .onErrorResume(TimeoutException.class, error ->
                            Mono.just(ResponseEntity.status(504).build())
                        )
                        .onErrorResume(error -> {
                            error.printStackTrace();
                            return Mono.just(ResponseEntity.status(500).build());
                        });
        return NPMResponse;
    }

}
