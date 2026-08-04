package com.giosmdev.package_radar.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucket4j.Bucket;
import reactor.core.publisher.Mono;

import com.giosmdev.package_radar.model.HealthScoreReport.HealthScoreReport;
//import com.giosmdev.package_radar.model.NPMRegistry.RawNpmPayload;
//import com.giosmdev.package_radar.model.universal.UniversalPackageFeatures;
import com.giosmdev.package_radar.service.ApiLayer.NPMService;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController //combines @Controller and @ResponseBody: Tells SB this class handles http requests and return JSON
@RequestMapping("/api/package") //maps http requests to specific controller classes: base url for all endpoints in this controller
public class PackageController {

    //Constructor injection: prefered way over auto injection
    private final NPMService npmService;
    private final Bucket requestBucket;

    public PackageController(NPMService npmService, Bucket requestBucket){
        this.npmService = npmService;
        this.requestBucket = requestBucket;
    }

    @GetMapping("/npm/{pkgName}")
    public Mono<ResponseEntity<HealthScoreReport>> getReport(@PathVariable String pkgName){
        if(requestBucket.tryConsume(1)){
            return npmService.getReport(pkgName).map(response -> {
                return ResponseEntity.status(HttpStatus.OK).body(response);
            });
        }
        return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null));
    }//#2d3e50

    @GetMapping("/npm/{scope}/{pkgName}")
    public Mono<ResponseEntity<HealthScoreReport>> getReport(@PathVariable String scope, @PathVariable String pkgName){
        System.out.println("Scoped Package");
        String concatenatedQuery = scope + "/" + pkgName;
        if(requestBucket.tryConsume(1)){
            return npmService.getReport(concatenatedQuery).map(response -> {
                return ResponseEntity.status(HttpStatus.OK).body(response);
            });
        }
        return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null));
    }

    
}
