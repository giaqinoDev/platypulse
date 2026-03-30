package com.giosmdev.package_radar.repository.RawNPMRegistryResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.model.NPMRegistry.LatestVersionInfo;
import com.giosmdev.package_radar.model.NPMRegistry.PackageInfo;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmResponse;
import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependenciesInfo;
import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependencyTree;
import com.giosmdev.package_radar.model.NPMRegistry.DownloadsTimeline.WeeklyDownloads;

import io.micrometer.core.ipc.http.HttpSender.Response;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Component
public class RawNPMRegistryResponse {
    private final WebClient webClient;
    private final BiFunction<String, String, Mono<LibrariesIOResponse>> librariesIOFetch;
    private final BiFunction<String, JsonNode, Mono<DependencyTree>> genDependenciesTree;

    private String BASE_PACKAGE_URL ="https://registry.npmjs.org";
    private String BASE_DOWNLOAD_URL = "https://api.npmjs.org/downloads/range/last-year";

    public RawNPMRegistryResponse(WebClient webClient, 
                                  BiFunction<String, String, Mono<LibrariesIOResponse>> librariesIOFetch,
                                  BiFunction<String, JsonNode, Mono<DependencyTree>> genDependenciesTree){
        this.webClient = webClient;
        this.librariesIOFetch = librariesIOFetch;
        this.genDependenciesTree = genDependenciesTree;
    }

    public Mono<RawNpmResponse> genRawNPMResponse(String pkgName){
        //Call NPM registry call
        Mono<PackageInfo> responseEntityMono = webClient.get() //builds an http GET request
                        .uri(BASE_PACKAGE_URL + "/{pkgName}", pkgName) //sets request path
                        //.header("Accept", "application/vnd.npm.install-v1+json")
                        .retrieve() // executes the request and prepare to extract the body
                        .onStatus( //handles HTTP status codes before the body is read
                            status -> status.is4xxClientError(),
                            response -> Mono.error(new PackageNotFoundException())
                        )
                        .onStatus(
                            status -> status.is5xxServerError(),
                            response -> Mono.error(new RegistryException())
                        )
                        .bodyToMono(JsonNode.class) //Convert the response body into a Mono<String>
                        .timeout(Duration.ofSeconds(3))
                        .flatMap(response -> {
                            return formatReponse(response);
                        });
        //Handle Any Errors
        Mono<ResponseEntity<PackageInfo>> npmRegResult = handleErrors(responseEntityMono);
                        

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
                            .timeout(Duration.ofSeconds(3))
                            .map(response -> {
                                return formatDownloadsTimeline(response);
                            });
        Mono<ResponseEntity<List<WeeklyDownloads>>> downloadsTimeline_result = handleErrors(downloadsTimeline);
        
        //Libraries 
        Mono<LibrariesIOResponse> libIO_response = librariesIOFetch.apply("npm", pkgName);
        Mono<ResponseEntity<LibrariesIOResponse>> libIO_result = handleErrors(libIO_response);

        // Merge downloads timeline into response
        // if any of the arguements in Mono.zip are Mono.empty() then Mono.zip will return empty
        Mono<RawNpmResponse> NPMResponse = Mono.zip(responseEntityMono, downloadsTimeline, libIO_response)
                                        .map(tuple -> {
                                            PackageInfo package_info = tuple.getT1();
                                            List<WeeklyDownloads> downloads_timeline = tuple.getT2();
                                            LibrariesIOResponse libIO_Response = tuple.getT3();
                                            return new RawNpmResponse(package_info, downloads_timeline, libIO_Response);
                                        });
        return NPMResponse;
    }

    //Packround-Check
    private Mono<PackageInfo> formatReponse(JsonNode response){
        //JsonNode represents a general abstract node in a Json tree structure and allows you to handle Json data dynamicaly
        String name = response.get("name").asString();
        String latest_v = response.get("dist-tags").get("latest").asString();
        JsonNode latest_v_reference = response.get("versions").get(latest_v);
        JsonNode repository = latest_v_reference.get("repository");
        JsonNode rawDependencies = latest_v_reference.get("dependencies");

        JsonNode keywords = latest_v_reference.get("keywords");
        JsonNode author = latest_v_reference.get("author");
        String license = latest_v_reference.get("license").asString();
        String homepage = latest_v_reference.get("homepage").asString();

        String node_version = latest_v_reference.get("_nodeVersion").asString();
        String npm_version = latest_v_reference.get("_npmVersion").asString();


        LatestVersionInfo latest_v_info = new LatestVersionInfo(
            repository, keywords, author, license, 
            homepage,node_version,npm_version, null,null);

        JsonNode versions_release_info = response.get("time");
        PackageInfo package_info = new PackageInfo(name, latest_v, null, versions_release_info);
        
        if(rawDependencies == null){
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
        return genDependenciesTree.apply(name, rawDependencies).map(tree -> {
            latest_v_info.setDependency_tree(tree);
            return latest_v_info;
        });
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
                                        
                                        return new DependenciesInfo(dependency_name, dependency_version_raw, deprecated, null);
                                    });
                        return dependency_info;
                    }
                ).collectList();
        return formattedDependencies;
    }

    private <T> Mono<ResponseEntity<T>> handleErrors(Mono<T> response){
        return response.map(data -> ResponseEntity.ok(data))
                        .onErrorResume(PackageNotFoundException.class, error ->
                            Mono.just(ResponseEntity.status(404).build())
                        )
                        .onErrorResume(RegistryException.class, error ->
                            Mono.just(ResponseEntity.status(503).build())
                        )
                        .onErrorResume(TimeoutException.class, error ->
                            Mono.just(ResponseEntity.status(504).build())
                        )
                        .onErrorResume(error ->
                            Mono.just(ResponseEntity.status(500).build())
                        );
    }
}
