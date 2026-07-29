package com.giosmdev.package_radar.service.ApiLayer;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;
import com.giosmdev.package_radar.mappers.UniversalSignalsEntityMapper;
import com.giosmdev.package_radar.model.DB_Entities.EcosystemEntity;
import com.giosmdev.package_radar.model.DB_Entities.PackageHealthSnapShots_Entity;
import com.giosmdev.package_radar.model.DB_Entities.PackagesEntity;
import com.giosmdev.package_radar.model.DB_Entities.RawSignals_Entity;
import com.giosmdev.package_radar.model.DB_Entities.UniversalSignals_Entity;
import com.giosmdev.package_radar.model.HealthScoreReport.HealthScoreReport;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmPayload;
import com.giosmdev.package_radar.model.universal.UniversalPackageFeatures;
import com.giosmdev.package_radar.repository.EcosystemRepository;
import com.giosmdev.package_radar.repository.PackageHealthScoreRepository;
import com.giosmdev.package_radar.repository.PackageRepository;
import com.giosmdev.package_radar.repository.RawSignalsRepository;
import com.giosmdev.package_radar.repository.UnviersalSignalsRepository;
//Repositories
import com.giosmdev.package_radar.service.RawNPMRegistryResponse.RawNPMRegistryResponse;
import com.giosmdev.package_radar.service.DB_Service.PlatyPulse_DB_Service;
import com.giosmdev.package_radar.service.DB_Service.PlatyPulse_DB_Service.LookupStatus;
import com.giosmdev.package_radar.service.DB_Service.PlatyPulse_DB_Service.PackageLookUpResult;
import com.giosmdev.package_radar.service.ExtractionLayer.SignalExtraction;
import com.giosmdev.package_radar.service.ScoringLayer.PackageHealthScoringService;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;


@Service
public class NPMService {
    private RawNPMRegistryResponse rawNPMRegResponse;
    private SignalExtraction signalExtractor;
    private PackageHealthScoringService scoringService;
    private PlatyPulse_DB_Service plp_db_service;

    //Repositories------------------------------------
    private EcosystemRepository ecosystem_repo;

    //Spring expects all constructor injections to be done in one consturctor
    public NPMService(
        RawNPMRegistryResponse rawNPMRegResponse, 
        SignalExtraction signalExtractor, 
        PackageHealthScoringService scoringService, 
        PlatyPulse_DB_Service plp_db_service, 
        EcosystemRepository ecosystem_repo
    ){
        this.rawNPMRegResponse = rawNPMRegResponse;
        this.ecosystem_repo = ecosystem_repo;
        this.signalExtractor = signalExtractor;
        this.scoringService = scoringService;
        this.plp_db_service = plp_db_service;
    }

    public Mono<ResponseEntity<HealthScoreReport>> getReport(String pkgName){
        PackageLookUpResult latest_report = plp_db_service.getHealthScore(pkgName);
        if(latest_report.status() == LookupStatus.REPORT_FOUND || latest_report.status() == LookupStatus.RAW_SIGNALS_MISSING){
            return Mono.just(ResponseEntity.ok(latest_report.report()));
        }
        Mono<ResponseEntity<HealthScoreReport>> generated_report = genReport(pkgName);
        return generated_report;
        
    }

    private Mono<ResponseEntity<HealthScoreReport>> genReport(String pkgName){
        Mono<ResponseEntity<RawNpmPayload>> raw_response = rawNPMRegResponse.genRawNPMResponse(pkgName);

        Mono<ResponseEntity<HealthScoreReport>> score_report_javaScript = raw_response.map((response) -> {
            if(response.getStatusCode().isSameCodeAs(HttpStatus.OK)){
                Optional<EcosystemEntity> ecosystem = ecosystem_repo.findById(1); //npm entry
                if(!ecosystem.isPresent()){
                    System.out.println("Ecosystem Does not Exist");
                    return ResponseEntity.status(404).build();
                }

                UniversalPackageFeatures extracted_signals = signalExtractor.extractSignals(response.getBody());
                HealthScoreReport generated_report = scoringService.score(extracted_signals, response.getBody());

                plp_db_service.writeToDB(pkgName, ecosystem.get(), generated_report, extracted_signals, response.getBody());

                return ResponseEntity.ok(generated_report);
            }
            return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(null);
        });

        return score_report_javaScript;
    }
}
