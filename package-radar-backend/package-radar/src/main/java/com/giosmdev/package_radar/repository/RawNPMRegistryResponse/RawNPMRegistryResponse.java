package com.giosmdev.package_radar.repository.RawNPMRegistryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.model.NPMRegistry.DependenciesInfo;
import com.giosmdev.package_radar.model.NPMRegistry.LatestVersionInfo;
import com.giosmdev.package_radar.model.NPMRegistry.PackageInfo;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmResponse;
import com.giosmdev.package_radar.model.NPMRegistry.DownloadsTimeline.WeeklyDownloads;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Component
public class RawNPMRegistryResponse {
    private final WebClient webClient;
    private final BiFunction<String, String, Mono<LibrariesIOResponse>> librariesIOFetch;

    private String BASE_PACKAGE_URL ="https://registry.npmjs.org";
    private String BASE_DOWNLOAD_URL = "https://api.npmjs.org/downloads/range/last-year";

    public RawNPMRegistryResponse(WebClient webClient, BiFunction<String, String, Mono<LibrariesIOResponse>> librariesIOFetch){
        this.webClient = webClient;
        this.librariesIOFetch = librariesIOFetch;
    }

    public Mono<RawNpmResponse> genRawNPMResponse(String pkgName){
        //Call NPM registry call
        Mono<PackageInfo> responseEntityMono = webClient.get() //builds an http GET request
                        .uri(BASE_PACKAGE_URL + "/{pkgName}", pkgName) //sets request path
                        //.header("Accept", "application/vnd.npm.install-v1+json")
                        .retrieve() // executes the request and prepare to extract the body
                        .onStatus( //handles HTTP status codes before the body is read
                            status -> status.is4xxClientError(),
                            response -> Mono.error(new RuntimeException("Package not found"))
                        )
                        .onStatus(
                            status -> status.is5xxServerError(),
                            response -> Mono.error(new RuntimeException("NPM registery error"))
                        )
                        .bodyToMono(JsonNode.class) //Convert the response body into a Mono<String>
                        .flatMap(response -> {
                            return formatReponse(response);
                        })
                        .onErrorResume(error -> {    //Reactive error handling: If any error happend upstream, switch to a fallback Mono
                            
                            if(error.getMessage().contains("Package not found")){
                                System.out.println("Package not found!!!!!!!!!!!!!!!!!!!!!!!!");
                                return Mono.just(new PackageInfo());//Mono.just("Oops! Looks like this package doesn't exist.");
                            }
                            else if(error.getMessage().contains("NPM registry error")){
                                System.out.println("NPM registry error!!!!!!!!!!!!!!!!!!!!!!!!");
                                return Mono.just(new PackageInfo());//Mono.just("NPM registry is havign some issues, check back later.");
                            }
                            else{
                                error.printStackTrace();
                                return Mono.just(new PackageInfo());//Mono.just("An unkown error has occured.");
                            }
                        });

        //NPM Registry Package downloads range call
        Mono<List<WeeklyDownloads>> downloadsTimeline = webClient.get()
                            .uri(BASE_DOWNLOAD_URL + "/{pkgName}", pkgName)
                            .retrieve()
                            .onStatus(
                                status -> status.is4xxClientError(),
                                response -> Mono.error(new RuntimeException("Package not found"))
                            )
                            .onStatus(
                                status -> status.is5xxServerError(),
                                response -> Mono.error(new RuntimeException("NPM Downloads Registry Error"))
                            )
                            .bodyToMono(JsonNode.class)
                            .map(response -> {
                                return formatDownloadsTimeline(response);
                            })
                            .onErrorResume(error -> {
                                if(error.getMessage().contains("Package not found")){
                                    return Mono.just(new ArrayList<WeeklyDownloads>());//Mono.just("Oops! Looks like this package doesn't exist.");
                                }
                                else if(error.getMessage().contains("NPM Downloads registry error")){
                                    return Mono.just(new ArrayList<WeeklyDownloads>());//Mono.just("NPM registry is havign some issues, check back later.");
                                }
                                else{
                                    return Mono.just(new ArrayList<WeeklyDownloads>());//Mono.just("An unkown error has occured.");
                                }
                            });
        
        //Libraries 
        Mono<LibrariesIOResponse> libIO_response = librariesIOFetch.apply("npm", pkgName);

        // Merge downloads timeline into response
        // if any of the arguements in Mono.zip are Mono.empty() then Mono.zip wil lreturn empty
        Mono<RawNpmResponse> NPMResponse = Mono.zip(responseEntityMono, downloadsTimeline, libIO_response)
                                        .map(tuple -> {
                                            PackageInfo package_info = tuple.getT1();
                                            List<WeeklyDownloads> downloads_timeline = tuple.getT2();
                                            LibrariesIOResponse libIO_Response = tuple.getT3();
                                            return new RawNpmResponse(package_info, downloads_timeline, libIO_Response);
                                        });
        return NPMResponse;
    }

    //Packground-Check
    private Mono<PackageInfo> formatReponse(JsonNode response){
        //JsonNode represents a general abstract node in a Json tree structure and allows you to handle Json data dynamicaly
        String name = response.get("name").asString();
        String latest_v = response.get("dist-tags").get("latest").asString();
        JsonNode repository = response.get("versions").get(latest_v).get("repository");
        JsonNode rawDependencies = response.get("versions").get(latest_v).get("dependencies");

        Mono<List<DependenciesInfo>> dependencies = formatDependenciesInfo(rawDependencies);
        LatestVersionInfo latest_v_info = new LatestVersionInfo(repository, null);
        JsonNode versions_release_info = response.get("time");
        PackageInfo package_info = new PackageInfo(name, latest_v, null, versions_release_info);

        //Package Doesnt has dependencies
        if(dependencies != null){
            Mono<LatestVersionInfo> latest_v_info_result = dependencies.map(tuples -> {
                latest_v_info.setDependencies(tuples);
                return latest_v_info;
            });

            Mono<PackageInfo> package_info_result = latest_v_info_result.map(tuples -> {
                package_info.setLatest_version_info(tuples);
                return package_info;
            });            
            return package_info_result;
        }

        package_info.setLatest_version_info(latest_v_info);
        //System.out.println(package_info);
        return Mono.just(package_info);
    }

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

    private Mono<List<DependenciesInfo>> formatDependenciesInfo(JsonNode dependencies){
        if(dependencies == null){
            return null;
        }

        //Flux.fromIterable() converts argurments into a flux stream
        Mono<List<DependenciesInfo>> formattedDependencies = Flux.fromIterable(dependencies.propertyNames())
                .flatMap(name -> {
                        String dependency_name = name;
                        String dependency_version_raw = dependencies.get(name).asString();
                        String dependency_version = dependency_version_raw.substring(1);

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
                                        
                                        return new DependenciesInfo(dependency_name, dependency_version_raw, deprecated);
                                    });
                        return dependency_info;
                    }
                ).collectList();
        return formattedDependencies;
    }
}
