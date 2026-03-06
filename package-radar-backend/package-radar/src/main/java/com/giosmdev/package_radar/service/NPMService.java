package com.giosmdev.package_radar.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

//Models
import com.giosmdev.package_radar.model.Report;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmResponse;
import com.giosmdev.package_radar.report_gen_layer.extract_layer.ReportContainer;
//Repositories
import com.giosmdev.package_radar.repository.PackageRepository;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class NPMService {
    private final PackageRepository packageRepo;
    private final ReportContainer extractNPMData;
    private final WebClient webClient;

    //Spring expects all constructor injections to be done in one consturctor
    public NPMService(PackageRepository packageRepo, WebClient webClient, ReportContainer extractNPMData){
        this.extractNPMData = extractNPMData;
        this.packageRepo = packageRepo;
        this.webClient = webClient;
    }

    public Mono<RawNpmResponse> getReport(String pkgName){

        //Call NPM registry
        Mono<RawNpmResponse> responseEntityMono = webClient.get() //builds an http GET request
                        .uri("https://registry.npmjs.org/{pkgName}", pkgName) //sets request path
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
                        .map(response -> {
                            return formatReponse(response);
                        })
                        .onErrorResume(error -> {    //Reactive error handling: If any error happend upstream, switch to a fallback Mono
                            if(error.getMessage().contains("Package not found")){
                                return null;//Mono.just("Oops! Looks like this package doesn't exist.");
                            }
                            else if(error.getMessage().contains("NPM registry error")){
                                return null;//Mono.just("NPM registry is havign some issues, check back later.");
                            }
                            else{
                                return null;//Mono.just("An unkown error has occured.");
                            }
                        });
        //
        // Mono<Object> ex = responseEntityMono.map(response -> {
        //     return extractNPMData.extract(response);
        // });
        // .map() is a transform operator in Reactor
        // It takes the value inside the Mono/Flux once it is emitted
        // It returns a new value, wrapped in the same reactive type (Mono or flux)
        // Use when transformation is synchronous
        return responseEntityMono;//extractNPMData.extract(responseEntityMono);
    }

    private RawNpmResponse formatReponse(JsonNode response){
        //JsonNode represents a general abstract node in a Json tree structure and allows you to handle Json data dynamicaly
        String name = response.get("name").asString();
        String latest_v = response.get("dist-tags").get("latest").asString();
        JsonNode latest_v_info = response.get("versions").get(latest_v);
        JsonNode versions_release_info = response.get("time");

        return new RawNpmResponse(name, latest_v, latest_v_info, versions_release_info);
    }

}
