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
    private PackageHealthScoreRepository pkgHealthRepo;
    private RawSignalsRepository rawSignalsRepo;
    private PackageRepository pkgRepo;
    private EcosystemRepository ecosystem_repo;
    private UnviersalSignalsRepository uni_signals_repo;

    //Mappers-----------------------------------------
    private ObjectMapper objectMapper = new ObjectMapper();
    private UniversalSignalsEntityMapper usE_Mapper;

    //Spring expects all constructor injections to be done in one consturctor
    public NPMService(
        RawNPMRegistryResponse rawNPMRegResponse, 
        SignalExtraction signalExtractor, 
        PackageHealthScoringService scoringService, 
        PlatyPulse_DB_Service plp_db_service, 
        EcosystemRepository ecosystem_repo,
        PackageRepository pkgRepo,
        UniversalSignalsEntityMapper usE_Mapper,
        UnviersalSignalsRepository uni_signals_repo,
        PackageHealthScoreRepository pkgHealthRepo,
        RawSignalsRepository rawSignalsRepo
    ){
        this.rawNPMRegResponse = rawNPMRegResponse;
        this.ecosystem_repo = ecosystem_repo;
        this.pkgRepo = pkgRepo;
        this.signalExtractor = signalExtractor;
        this.scoringService = scoringService;
        this.plp_db_service = plp_db_service;
        this.usE_Mapper = usE_Mapper;
        this.uni_signals_repo = uni_signals_repo;
        this.pkgHealthRepo = pkgHealthRepo;
        this.rawSignalsRepo = rawSignalsRepo;
    }

    public Mono<ResponseEntity<HealthScoreReport>> getReport(String pkgName){
        PackageLookUpResult latest_report = plp_db_service.getHealthScore(pkgName);
        if(latest_report.status() == LookupStatus.REPORT_FOUND || latest_report.status() == LookupStatus.RAW_SIGNALS_MISSING){
            System.out.println("FOUND_____________________________________________________________________________");
            return Mono.just(ResponseEntity.ok(latest_report.report()));
        }
        Mono<ResponseEntity<HealthScoreReport>> generated_report = genReport(pkgName);
        System.out.println("GENERATED: " + latest_report.status() + "------------------------------------------------------------------------------");
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

                //DB Entity Mapping----------------------------------------------------------------------
                Instant timeInstant = Instant.now();
                PackageHealthSnapShots_Entity snapShot = new PackageHealthSnapShots_Entity();
                snapShot.setAggregate_score(BigDecimal.valueOf(generated_report.getOverall()));
                snapShot.setActivity_score(BigDecimal.valueOf(generated_report.getActivity()));
                snapShot.setMaintenance_score(BigDecimal.valueOf(generated_report.getMaintenance()));
                snapShot.setAdoption_score(BigDecimal.valueOf(generated_report.getAdoption()));
                snapShot.setRisk_score(BigDecimal.valueOf(generated_report.getRisk()));
                snapShot.setCreated_at(timeInstant);
                snapShot.setAlgorithm_v(System.getenv("ALGORITHM_VERSION"));

                UniversalSignals_Entity universal_signals = usE_Mapper.toEntity(extracted_signals);
                universal_signals.setCalculated_at(timeInstant);
                
                
                RawSignals_Entity raw_signals = new RawSignals_Entity();
                raw_signals.setFetched_at(timeInstant);
                JsonNode ex = objectMapper.valueToTree(response.getBody());
                raw_signals.setRaw_data(ex);
                

                Optional<PackagesEntity> queried_pkg = pkgRepo.findByPackageNameAndEcosystem_id(pkgName, 1);
                PackagesEntity pkg;
                if(!queried_pkg.isPresent()){
                    pkg = new PackagesEntity(null, ecosystem.get(), pkgName, null);
                }
                else{
                    pkg = queried_pkg.get();
                }

                pkg = pkgRepo.save(pkg);
                snapShot.setPkg(pkg);
                snapShot= pkgHealthRepo.save(snapShot);

                universal_signals.setPkgHealthSnapShot(snapShot);
                raw_signals.setPkgHealthSnapShot(snapShot);

                uni_signals_repo.save(universal_signals);
                System.out.println("-----------------------------" + raw_signals.getRaw_data().getClass());
                rawSignalsRepo.save(raw_signals);

                pkg.setLatest_report(snapShot);
                pkgRepo.save(pkg);

                return ResponseEntity.ok(generated_report);
            }
            return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(null);
        });

        return score_report_javaScript;
    }
}
