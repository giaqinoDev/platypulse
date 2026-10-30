package com.giosmdev.package_radar.controller;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucket4j.Bucket;
import reactor.core.publisher.Mono;

import com.giosmdev.package_radar.dto.ApiResponse;
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

    @GetMapping("/npm/{pkgName}/latest")
    public Mono<ResponseEntity<ApiResponse<HealthScoreReport>>> getLatestReport(@PathVariable String pkgName){
        return condensedGetLatest(pkgName);
    }

    @GetMapping("/npm/{scope}/{pkgName}/latest")
    public Mono<ResponseEntity<ApiResponse<HealthScoreReport>>> getLatestReport(@PathVariable String scope, @PathVariable String pkgName){
        System.out.println("Scoped Package");
        String concatenatedQuery = scope + "/" + pkgName;
        return condensedGetLatest(concatenatedQuery);
    }

    @GetMapping("/npm/{pkgName}/id/{report_id}")
    public Mono<ResponseEntity<ApiResponse<HealthScoreReport>>> getReportbyId(@PathVariable String pkgName, @PathVariable UUID report_id){
        return npmService.getReportById(report_id).map((response) -> {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        })
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/npm/{scope}/{pkgName}/id/{report_id}")
    public Mono<ResponseEntity<ApiResponse<HealthScoreReport>>> getReportbyId(@PathVariable String scope, @PathVariable String pkgName, @PathVariable UUID report_id){
        return npmService.getReportById(report_id).map((response) -> {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        })
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping("/npm/{pkgId}/generateReport")
    public Mono<ResponseEntity<UUID>> generateNewReport(@PathVariable UUID pkgId){
        System.out.println("GENERATING NEW REPORT!!!");
        if(requestBucket.tryConsume(1)){
            return npmService.genReportByPkgId(pkgId).map(response -> {
                return ResponseEntity.status(HttpStatus.OK).body(response.getData().getReport_id());
            })
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.FORBIDDEN).body(null));
        }
        return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null));
    }

    private Mono<ResponseEntity<ApiResponse<HealthScoreReport>>> condensedGetLatest(String concatenatedQuery){
        if(requestBucket.tryConsume(1)){
            return npmService.getLatestReport(concatenatedQuery).map(response -> {
                return ResponseEntity.status(HttpStatus.OK).body(response);
            });
        }
        return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null));
    }
}
