package com.giosmdev.package_radar.service.RawNPMRegistryResponse;

import java.util.List;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.configs.APIResponseLayer.FallbackFetch;
import com.giosmdev.package_radar.model.GitHubRegistry.RawGitHubResponse;
import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.model.NPMRegistry.PackageInfo;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmPayload;
import com.giosmdev.package_radar.model.NPMRegistry.DownloadsTimeline.WeeklyDownloads;
import com.giosmdev.package_radar.service.ApiLayer.GitHubService;
import com.giosmdev.package_radar.service.ApiLayer.NPMLayerApis.LibrariesIoAPI;
import com.giosmdev.package_radar.service.ApiLayer.NPMLayerApis.NPMRegistryAPI;

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

    public Mono<RawNpmPayload> genRawNPMResponse(String pkgName){
        //Call NPM registry call
        Mono<PackageInfo> npm_reg_response = npmRegistryAPI.getPackageInfo(pkgName);

        Mono<List<WeeklyDownloads>> npm_reg_dwnld_timeline = npmRegistryAPI.getDownloadsTimeline(pkgName);
        
        //Libraries 
        Mono<FallbackFetch<LibrariesIOResponse>> libIO_response = librariesIOResponse.fetch("npm", pkgName);

        // Merge downloads timeline into response
        // if any of the arguements in Mono.zip are Mono.empty() then Mono.zip will return empty
        Mono<RawNpmPayload> NPMResponse = npm_reg_response.flatMap(package_info -> {
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
            Mono<FallbackFetch<RawGitHubResponse>> gitHubApiResponse = gitHubService.getGitHubResponse(group, repo);
            
            return Mono.zip(npm_reg_dwnld_timeline, libIO_response, gitHubApiResponse)
                    .map(tuple -> {
                        RawGitHubResponse gitHub_response = tuple.getT3().getData();
                        System.out.println("github api repsonse status: " + tuple.getT3().getException_class());

                        List<WeeklyDownloads> downloads_timeline = tuple.getT1();

                        LibrariesIOResponse libIO_Response = tuple.getT2().getData();

                        return new RawNpmPayload(package_info, downloads_timeline, libIO_Response, gitHub_response);
                    });
            }
        );
        return NPMResponse;
    }

}
