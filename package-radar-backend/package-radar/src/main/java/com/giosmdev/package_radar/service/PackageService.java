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

    public Report getReport(String pkgName){
        return webClient.get().uri("https://registry.npmjs.org/{pkgName}/latest", pkgName).retrieve().bodyToMono(Report.class).block();
    }
}
