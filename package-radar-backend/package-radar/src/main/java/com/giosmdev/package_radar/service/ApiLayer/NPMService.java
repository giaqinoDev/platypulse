package com.giosmdev.package_radar.service.ApiLayer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.model.HealthScoreReport.HealthScoreReport;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmPayload;
import com.giosmdev.package_radar.model.universal.UniversalPackageFeatures;
//Repositories
import com.giosmdev.package_radar.repository.RawNPMRegistryResponse.RawNPMRegistryResponse;
import com.giosmdev.package_radar.service.ExtractionLayer.SignalExtraction;
import com.giosmdev.package_radar.service.ScoringLayer.PackageHealthScoringService;

import reactor.core.publisher.Mono;


@Service
public class NPMService {
    private RawNPMRegistryResponse rawNPMRegResponse;
    private SignalExtraction signalExtractor;
    private PackageHealthScoringService scoringService;

    //Spring expects all constructor injections to be done in one consturctor
    public NPMService(RawNPMRegistryResponse rawNPMRegResponse, SignalExtraction signalExtractor, PackageHealthScoringService scoringService){
        this.rawNPMRegResponse = rawNPMRegResponse;
        this.signalExtractor = signalExtractor;
        this.scoringService = scoringService;
    }

    public Mono<ResponseEntity<HealthScoreReport>> getReport(String pkgName){
        Mono<ResponseEntity<RawNpmPayload>> raw_response = rawNPMRegResponse.genRawNPMResponse(pkgName);
        
        // Mono<ResponseEntity<UniversalPackageFeatures>> extracted_signals = raw_response.map((response) -> {
        //     return ResponseEntity.ok(signalExtractor.extractSignals(response.getBody()));
        // });

        Mono<ResponseEntity<HealthScoreReport>> score_report_javaScript = raw_response.map((response) -> {
            if(response.getStatusCode().isSameCodeAs(HttpStatus.OK)){
                UniversalPackageFeatures extracted_signals = signalExtractor.extractSignals(response.getBody());
                return ResponseEntity.ok(scoringService.score(extracted_signals));
            }
            System.out.println(response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(null);
        });

        return score_report_javaScript;
        //return raw_response;
    }
}
