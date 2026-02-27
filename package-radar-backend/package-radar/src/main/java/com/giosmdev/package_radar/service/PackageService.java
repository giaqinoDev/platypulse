package com.giosmdev.package_radar.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

//Models
import com.giosmdev.package_radar.model.Report;

//Repositories
import com.giosmdev.package_radar.repository.PackageRepository;
@Service
public class PackageService {
    private final PackageRepository packageRepo;
    private final WebClient webClient;

    //Spring expects all constructor injections to be done in one consturctor
    public PackageService(PackageRepository packageRepo, WebClient webClient){
        this.packageRepo = packageRepo;
        this.webClient = webClient;
    }

    public String getReport(String pkgName){
        String response = webClient.get()
                            .uri("https://registry.npmjs.org/{pkgName}", pkgName)
                            .header("Accept", "application/vnd.npm.install-v1+json")
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
        System.out.println("Response length: " + response.length());
        return response;
    }
}
