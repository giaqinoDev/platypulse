package com.giosmdev.package_radar.service.ApiLayer;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.dto.PackageAndSnapShotIDs;
import com.giosmdev.package_radar.exceptions.DataBaseExceptions.EcosystemNotFoundException;
import com.giosmdev.package_radar.model.DB_Entities.EcosystemEntity;
import com.giosmdev.package_radar.model.HealthScoreReport.HealthScoreReport;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmPayload;
import com.giosmdev.package_radar.model.universal.UniversalPackageFeatures;
import com.giosmdev.package_radar.repository.EcosystemRepository;
//Repositories
import com.giosmdev.package_radar.service.RawNPMRegistryResponse.RawNPMRegistryResponse;
import com.giosmdev.package_radar.service.DB_Service.PlatyPulse_DB_Service;
import com.giosmdev.package_radar.service.DB_Service.PlatyPulse_DB_Service.LookupStatus;
import com.giosmdev.package_radar.service.DB_Service.PlatyPulse_DB_Service.PackageLookUpResult;
import com.giosmdev.package_radar.service.ExtractionLayer.SignalExtraction;
import com.giosmdev.package_radar.service.ScoringLayer.PackageHealthScoringService;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;


@Service
public class NPMService {
    private RawNPMRegistryResponse rawNPMRegResponse;
    private SignalExtraction signalExtractor;
    private PackageHealthScoringService scoringService;
    private PlatyPulse_DB_Service plp_db_service;
    private HttpServletRequest requestServlet;


    //Repositories------------------------------------
    private EcosystemRepository ecosystem_repo;

    //Spring expects all constructor injections to be done in one consturctor
    public NPMService(
        RawNPMRegistryResponse rawNPMRegResponse, 
        SignalExtraction signalExtractor, 
        PackageHealthScoringService scoringService, 
        PlatyPulse_DB_Service plp_db_service, 
        EcosystemRepository ecosystem_repo,
        HttpServletRequest requestServlet
    ){
        this.rawNPMRegResponse = rawNPMRegResponse;
        this.ecosystem_repo = ecosystem_repo;
        this.signalExtractor = signalExtractor;
        this.scoringService = scoringService;
        this.plp_db_service = plp_db_service;
        this.requestServlet = requestServlet;
    }

    public Mono<HealthScoreReport> getReport(String pkgName){

        PackageLookUpResult latest_report = plp_db_service.getHealthScore(pkgName);
        if(latest_report.status() == LookupStatus.REPORT_FOUND || latest_report.status() == LookupStatus.RAW_SIGNALS_MISSING){
            return Mono.just(latest_report.report());
        }
        Mono<HealthScoreReport> generated_report = genReport(pkgName);
        return generated_report;
    }

    private Mono<HealthScoreReport> genReport(String pkgName){
        Mono<RawNpmPayload> raw_response = rawNPMRegResponse.genRawNPMResponse(pkgName);
        Mono<HealthScoreReport> score_report_javaScript = raw_response.map((response) -> {
            Optional<EcosystemEntity> ecosystem = ecosystem_repo.findById(1); //npm entry
            if(!ecosystem.isPresent()){
                System.out.println("Ecosystem Does not Exist");
                throw new EcosystemNotFoundException("Ecosystem not found in DB", requestServlet.getRequestURI().toString());
            }

            UniversalPackageFeatures extracted_signals = signalExtractor.extractSignals(response);
            HealthScoreReport generated_report = scoringService.score(extracted_signals, response);

            PackageAndSnapShotIDs ids = plp_db_service.writeToDB(pkgName, ecosystem.get(), generated_report, extracted_signals, response);

            generated_report.setReport_id(ids.getReport_id());
            generated_report.setPackage_id(ids.getPackage_id());
            return generated_report;
        });

        return score_report_javaScript;
    }
}
