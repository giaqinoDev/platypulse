package com.giosmdev.package_radar.service.ApiLayer;
import java.lang.foreign.Linker.Option;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.dto.ApiResponse;
import com.giosmdev.package_radar.dto.PackageAndSnapShotIDs;
import com.giosmdev.package_radar.exceptions.DataBaseExceptions.EcosystemNotFoundException;
import com.giosmdev.package_radar.model.DB_Entities.EcosystemEntity;
import com.giosmdev.package_radar.model.DB_Entities.PackagesEntity;
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
import com.giosmdev.package_radar.repository.PackageHealthScoreRepository;
import com.giosmdev.package_radar.repository.PackageRepository;

@Service
public class NPMService {
    private RawNPMRegistryResponse rawNPMRegResponse;
    private SignalExtraction signalExtractor;
    private PackageHealthScoringService scoringService;
    private PlatyPulse_DB_Service plp_db_service;
    private HttpServletRequest requestServlet;
    private PackageHealthScoreRepository pkg_health_score_repo;
    private PackageRepository pkg_repo;


    //Repositories------------------------------------
    private EcosystemRepository ecosystem_repo;

    //Spring expects all constructor injections to be done in one consturctor
    public NPMService(
        RawNPMRegistryResponse rawNPMRegResponse, 
        SignalExtraction signalExtractor, 
        PackageHealthScoringService scoringService, 
        PlatyPulse_DB_Service plp_db_service, 
        EcosystemRepository ecosystem_repo,
        HttpServletRequest requestServlet,
        PackageHealthScoreRepository pkg_health_score_repo,
        PackageRepository pkg_repo
    ){
        this.rawNPMRegResponse = rawNPMRegResponse;
        this.ecosystem_repo = ecosystem_repo;
        this.signalExtractor = signalExtractor;
        this.scoringService = scoringService;
        this.plp_db_service = plp_db_service;
        this.requestServlet = requestServlet;
        this.pkg_health_score_repo = pkg_health_score_repo;
        this.pkg_repo = pkg_repo;
    }

    public Mono<ApiResponse<HealthScoreReport>> getLatestReport(String pkgName){

        PackageLookUpResult latest_report = plp_db_service.getLatestHealthScore(pkgName);
        if(latest_report.status() == LookupStatus.REPORT_FOUND || latest_report.status() == LookupStatus.RAW_SIGNALS_MISSING){
            ApiResponse<HealthScoreReport> api_response = ApiResponse.success(latest_report.report().getReport_id().toString(), latest_report.report(), "success");
            return Mono.just(api_response);
        }
        Mono<ApiResponse<HealthScoreReport>> generated_report = genReportByPkgName(pkgName);
        return generated_report;
    }

    public Mono<ApiResponse<HealthScoreReport>> getReportById(UUID report_id){
        if(report_id == null){
            return Mono.empty();
        }
        PackageLookUpResult report = plp_db_service.getReportById(report_id);
        if(report.status() == LookupStatus.REPORT_FOUND || report.status() == LookupStatus.RAW_SIGNALS_MISSING){
            ApiResponse<HealthScoreReport> api_response = ApiResponse.success(report.report().getReport_id().toString(), report.report(), "success");
            return Mono.just(api_response);
        }
        return Mono.empty();
    }

    public Mono<ApiResponse<HealthScoreReport>> genReportByPkgId(UUID pkgId){
        Optional<PackagesEntity> package_entity = pkg_repo.findById(pkgId);
        if(package_entity.isPresent()){
            System.out.println("pkgName exists");
            String package_name = package_entity.get().getPackageName();
            return genReportByPkgName(package_name);
        }
        else{
            System.out.println("pkg name DNE");
        }
        return Mono.empty();
    }
    public Mono<ApiResponse<HealthScoreReport>> genReportByPkgName(String pkgName){
        System.out.println("GENERATING REPORT");
        PackageLookUpResult latest_report = plp_db_service.getLatestHealthScore(pkgName);
        boolean can_generate_new_report = true;
        if(latest_report.status() == LookupStatus.REPORT_FOUND || latest_report.status() == LookupStatus.RAW_SIGNALS_MISSING){
            can_generate_new_report = latest_report.report().getCan_analyze();
        }
        if(!can_generate_new_report){
            System.out.println("EMPTY");
            return Mono.empty();
        }

        Mono<RawNpmPayload> raw_response = rawNPMRegResponse.genRawNPMResponse(pkgName);
        Mono<ApiResponse<HealthScoreReport>> score_report_javaScript = raw_response.map((response) -> {
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
            ApiResponse<HealthScoreReport> api_response = ApiResponse.success(ids.getReport_id().toString(), generated_report, "success");
            return api_response; //generated_report;
        });

        return score_report_javaScript;
    }
}
