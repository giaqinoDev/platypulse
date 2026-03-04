package com.giosmdev.package_radar.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

//Models
import com.giosmdev.package_radar.model.Report;
import com.giosmdev.package_radar.report_gen_layer.extract_layer.ReportContainer;
//Repositories
import com.giosmdev.package_radar.repository.PackageRepository;

import io.micrometer.core.ipc.http.HttpSender.Response;
import reactor.core.publisher.Mono;
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

    public Mono<Object> getReport(String pkgName){
        Mono<Object> responseEntityMono = webClient.get() //builds an http GET request
                        .uri("https://registry.npmjs.org/{pkgName}", pkgName) //sets request path
                        .header("Accept", "application/vnd.npm.install-v1+json")
                        .retrieve() // executes the request and prepare to extract the body
                        .onStatus( //handles HTTP status codes before the body is read
                            status -> status.is4xxClientError(),
                            response -> Mono.error(new RuntimeException("Package not found"))
                        )
                        .onStatus(
                            status -> status.is5xxServerError(),
                            response -> Mono.error(new RuntimeException("NPM registery error"))
                        )
                        .bodyToMono(Object.class) //Convert the response body into a Mono<String>
                        .onErrorResume(error -> {    //Reactive error handling: If any error happend upstream, switch to a fallback Mono
                            if(error.getMessage().contains("Package not found")){
                                return Mono.just("Oops! Looks like this package doesn't exist.");
                            }
                            else if(error.getMessage().contains("NPM registry error")){
                                return Mono.just("NPM registry is havign some issues, check back later.");
                            }
                            else{
                                return Mono.just("An unkown error has occured.");
                            }
                        });
        return responseEntityMono;//extractNPMData.extract(responseEntityMono);
    }

}
