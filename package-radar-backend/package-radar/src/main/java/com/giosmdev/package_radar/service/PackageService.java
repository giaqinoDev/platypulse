package com.giosmdev.package_radar.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

//Models
import com.giosmdev.package_radar.model.Report;

//Repositories
import com.giosmdev.package_radar.repository.PackageRepository;

import io.micrometer.core.ipc.http.HttpSender.Response;
import reactor.core.publisher.Mono;
@Service
public class PackageService {
    private final PackageRepository packageRepo;
    private final WebClient webClient;

    //Spring expects all constructor injections to be done in one consturctor
    public PackageService(PackageRepository packageRepo, WebClient webClient){
        this.packageRepo = packageRepo;
        this.webClient = webClient;
    }

    public Mono<String> getReport(String pkgName){
        Mono<String> responseEntityMono = webClient.get()
                        .uri("https://registry.npmjs.org/{pkgName}", pkgName)
                        .header("Accept", "application/vnd.npm.install-v1+json")
                        .retrieve()
                        .bodyToMono(String.class)
                        .onErrorResume(error -> {
                            System.out.println("Error occurred: " + error.getMessage());
                            return Mono.empty();
                        });
        return responseEntityMono;
    }
}
