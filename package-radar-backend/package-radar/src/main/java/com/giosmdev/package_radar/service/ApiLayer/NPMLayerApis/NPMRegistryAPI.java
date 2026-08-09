package com.giosmdev.package_radar.service.ApiLayer.NPMLayerApis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiNotFoundException;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiRateLimitException;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiUnavailableException;
import com.giosmdev.package_radar.model.NPMRegistry.LatestVersionInfo;
import com.giosmdev.package_radar.model.NPMRegistry.PackageInfo;
import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependenciesInfo;
import com.giosmdev.package_radar.model.NPMRegistry.DownloadsTimeline.WeeklyDownloads;
import com.giosmdev.package_radar.service.ApiLayer.ApiErrorHandling.ApiErrorInterceptor;
import com.giosmdev.package_radar.service.ApiLayer.DependencyTree.GenDependenciesTree;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class NPMRegistryAPI {
    private final WebClient webClient;
    private final GenDependenciesTree genDependenciesTree;
    private final ApiErrorInterceptor apiErrorHandler;
    private final String BASE_PACKAGE_URL = "https://registry.npmjs.org";
    private final String BASE_DOWNLOAD_URL = "https://api.npmjs.org/downloads/range/last-year";

    public NPMRegistryAPI(WebClient webClient, GenDependenciesTree genDependenciesTree, ApiErrorInterceptor apiErrorHandler){
        this.webClient = webClient;
        this.genDependenciesTree = genDependenciesTree;
        this.apiErrorHandler = apiErrorHandler;
    }

    public Mono<PackageInfo> getPackageInfo(String pkgName){
        System.out.println(pkgName);
        String route = BASE_PACKAGE_URL + "/" + pkgName;
        Mono<PackageInfo> npm_package_info = webClient.get() //builds an http GET request
                        .uri(route) //sets request path
                        //.header("Accept", "application/vnd.npm.install-v1+json")
                        .retrieve() // executes the request and prepare to extract the body
                        .onStatus( //handles HTTP status codes before the body is read
                            status -> status.value() == 404,
                            response -> {
                                System.out.println("404 Error: NPM Package not found");
                                return Mono.error(new ExternalApiNotFoundException(pkgName + "NPM package not found", route));
                            }
                        )
                        .onStatus(
                            status -> status.value() == 429,
                            response -> {
                                System.out.println("429 Error: NPM Registry rate limit exceeded");
                                return Mono.error(new ExternalApiRateLimitException("NMP Registry rate limit exceeded", route));
                            }
                        )
                        .onStatus(
                            status -> status.is5xxServerError(),
                            response -> {
                                System.out.println("500 Level Error");
                                return Mono.error(new ExternalApiUnavailableException("NPM Registry unavailable", route));
                            }
                        )
                        .bodyToMono(JsonNode.class) //Convert the response body into a Mono<String>
                        .timeout(Duration.ofSeconds(10))
                        .flatMap(response -> formatReponse(response));
        return npm_package_info;
    }

    public Mono<List<WeeklyDownloads>> getDownloadsTimeline(String pkgName){
        //NPM Registry Package downloads range call
        String route = BASE_DOWNLOAD_URL + "/" + pkgName;
        Mono<List<WeeklyDownloads>> dwnld_timeline = webClient.get()
                            .uri(route)
                            .retrieve()
                            .onStatus(
                                status -> status.value() == 404,
                                response -> Mono.error(new ExternalApiNotFoundException("NPM package not found", route))
                            )
                            .onStatus(
                                status -> status.value() == 429,
                                response -> Mono.error(new ExternalApiRateLimitException("NMP Registry rate limit exceeded", route))
                            )
                            .onStatus(
                                status -> status.is5xxServerError(),
                                response -> {
                                    System.out.println("500 Level Error");
                                    return Mono.error(new ExternalApiUnavailableException("NPM Registry unavailable", route));
                                }
                            )
                            .bodyToMono(JsonNode.class)
                            .timeout(Duration.ofSeconds(10))
                            .map(response -> formatDownloadsTimeline(response));
        return dwnld_timeline;
        //Mono<ApiStatus<List<WeeklyDownloads>>> wrapped_result = apiErrorHandler.handle(dwnld_timeline);
        //return wrapped_result;
    
    }
    // Package Info Utility Functions-------------------------------------------------------------------------------------------------
    private Mono<PackageInfo> formatReponse(JsonNode response){   
        //JsonNode represents a general abstract node in a Json tree structure and allows you to handle Json data dynamicaly
        String name = response.path("name").asString(null);
        String description = response.path("description").asString(null);
        String latest_v = response.path("dist-tags").path("latest").asString(null);
        JsonNode latest_v_reference = response.get("versions").get(latest_v);
        JsonNode repository = latest_v_reference.get("repository");
        JsonNode rawDependencies = latest_v_reference.get("dependencies");

        JsonNode keywords = latest_v_reference.get("keywords");
        JsonNode author = latest_v_reference.get("author");
        String license = latest_v_reference.path("license").asString(null);
        String homepage = latest_v_reference.path("homepage").asString(null);

        String node_version = latest_v_reference.path("_nodeVersion").asString(null);
        String npm_version = latest_v_reference.path("_npmVersion").asString(null);


        LatestVersionInfo latest_v_info = new LatestVersionInfo(
            repository, keywords, author, license, 
            homepage,node_version,npm_version, null,null);

        JsonNode versions_release_info = response.get("time");
        PackageInfo package_info = new PackageInfo(name, description, latest_v, null, versions_release_info);
        
        if(rawDependencies == null || rawDependencies.isEmpty()){
            latest_v_info.setDependencies(null);
            latest_v_info.setDependency_tree(null);
            package_info.setLatest_version_info(latest_v_info);
            return Mono.just(package_info);
        }

        return formatDependenciesInfo(rawDependencies).flatMap(deps -> { //use .map when cuttin tree out
            latest_v_info.setDependencies(deps);
            //return latest_v_info; //Use if you choose to cut out dependency trees as a whole
            return buildLatestVersionDepTree(latest_v_info, name, rawDependencies);
        })
        .map(updatedLatest -> {
            package_info.setLatest_version_info(updatedLatest);
            return package_info;
        });
    }

    private Mono<LatestVersionInfo> buildLatestVersionDepTree(LatestVersionInfo latest_v_info, String name, JsonNode rawDependencies){
        return genDependenciesTree.generate(name, rawDependencies).map(tree -> {
            latest_v_info.setDependency_tree(tree);
            return latest_v_info;
        });
    }

    private Mono<List<DependenciesInfo>> formatDependenciesInfo(JsonNode dependencies){

        if(dependencies == null){
            return null;
        }

        //Flux.fromIterable() converts argurments into a flux stream
        Mono<List<DependenciesInfo>> formattedDependencies = Flux.fromIterable(dependencies.propertyNames())
                .flatMap(name -> {
                        String dependency_name = name;
                        String dependency_version_raw = dependencies.get(name).asString();
                        String dependency_version;
                        if(dependency_version_raw.length() > 0){
                            dependency_version = dependency_version_raw.substring(1);
                        }
                        else{
                            dependency_version = null;
                        }

                        Mono<DependenciesInfo> dependency_info = webClient.get()
                                    .uri(BASE_PACKAGE_URL + "/{dependency_name}", dependency_name)
                                    .retrieve()
                                    .onStatus(
                                        status -> status.is4xxClientError(),
                                        response -> Mono.error(new RuntimeException("Package not found"))
                                    )
                                    .onStatus(
                                        status -> status.is5xxServerError(),
                                        response -> Mono.error(new RuntimeException("NPM Registry Error"))
                                    )
                                    .bodyToMono(JsonNode.class)
                                    .map(response -> {
                                        Boolean deprecated;
                                        //Try catch needed in order to accoutn for "*" version types
                                        try{
                                            deprecated = (response.get("versions").get(dependency_version).has("deprecated")) ? true:false;
                                        }
                                        catch(Exception error){
                                            deprecated = false;
                                        }
                                        
                                        return new DependenciesInfo(dependency_name, dependency_version_raw, deprecated, null);
                                    });
                        return dependency_info;
                    }
                ).collectList();
        return formattedDependencies;
    }

    // Package Downloads Timeline Utility Functions-----------------------------------------------------------------------------------
    private List<WeeklyDownloads> formatDownloadsTimeline(JsonNode response){
        JsonNode downloads = response.get("downloads");
        int weekday_count = 0;
        int timeline_index = 0;
        int downloads_sum = 0;

        //Calculate number of weeks based off available timeline
        int num_weeks = (int) Math.ceil((double) downloads.size()/7);
        List<WeeklyDownloads> downloads_timeline = new ArrayList<WeeklyDownloads>(num_weeks);
        
        //Populate timeline
        for(int index = 0; index < num_weeks; ++index){
            downloads_timeline.add(new WeeklyDownloads());
        }

        //Insert into timeline
        for(int index = 0; index < downloads.size(); ++index){
            downloads_sum += downloads.get(index).get("downloads").asInt();
            if(weekday_count == 0){
                downloads_timeline.get(timeline_index).setStart_date(downloads.get(index).get("day").asString());
                weekday_count++;
            }
            else if(weekday_count == 6){
                downloads_timeline.get(timeline_index).setEnd_date(downloads.get(index).get("day").asString());
                downloads_timeline.get(timeline_index).setVolume(downloads_sum);
                downloads_sum = 0;
                timeline_index++;
                weekday_count = 0;
            }
            else{
                weekday_count++;
            }
        }
        return downloads_timeline;
    }
}
