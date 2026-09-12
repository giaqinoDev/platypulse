package com.giosmdev.package_radar.service.DB_Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.dto.PackageAndSnapShotIDs;
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

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class PlatyPulse_DB_Service {

    //Repos
    private EcosystemRepository ecosystemRepo;
    private PackageHealthScoreRepository pkgHealthRepo;
    private RawSignalsRepository rawSignalsRepo;
    private PackageRepository pkgRepo;
    private UnviersalSignalsRepository uni_signals_repo;

    @Value("${hr_duration_between_analysis}")
    private double time_between_analysis;//Double.parseDouble(System.getenv("HR_DURATION_BETWEEN_REPORT_ANALYZING"));

    //Mappers-----------------------------------------
    private UniversalSignalsEntityMapper usE_Mapper;
    private ObjectMapper objectMapper = new ObjectMapper();

    //Look up statuses--------------------------------------------------------------------------------------------------------------------------------------------------------
    public enum LookupStatus{
        PACKAGE_NOT_FOUND,
        REPORT_NOT_GENERATED,
        RAW_SIGNALS_MISSING,
        REPORT_FOUND
    }

    public record PackageLookUpResult(LookupStatus status, PackagesEntity pkg, HealthScoreReport report) {}
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public PlatyPulse_DB_Service(
        EcosystemRepository ecosystemRepo, 
        PackageRepository pkgRepo, 
        PackageHealthScoreRepository pkgHealthRepo, 
        RawSignalsRepository rawSignalsRepo,
        UnviersalSignalsRepository uni_signals_repo,

        UniversalSignalsEntityMapper usE_Mapper
    ){
        this.pkgRepo = pkgRepo;
        this.ecosystemRepo = ecosystemRepo;
        this.pkgHealthRepo = pkgHealthRepo;
        this.rawSignalsRepo = rawSignalsRepo;
        this.uni_signals_repo = uni_signals_repo;

        this.usE_Mapper = usE_Mapper;
    }

    public List<EcosystemEntity> getAllEcosystems(){
        return ecosystemRepo.findAll();
    }

    public PackageLookUpResult getHealthScore(String pkgName){
        Optional<PackagesEntity> queriedPkg = pkgRepo.findByPackageNameAndEcosystem_id(pkgName, 1);
        if(!queriedPkg.isPresent()){
            //Package does not exist
            return new PackageLookUpResult(LookupStatus.PACKAGE_NOT_FOUND, null, null);
        }
        PackagesEntity pkg = queriedPkg.get(); //gets the result of query
        PackageHealthSnapShots_Entity latest_snapShot = pkg.getLatest_report();
        if(latest_snapShot == null){
            //Report doesnt exist
            return new PackageLookUpResult(LookupStatus.REPORT_NOT_GENERATED, pkg, null);
        }
        Optional<RawSignals_Entity> raw_signals = rawSignalsRepo.findBypkgHealthSnapShot_id(latest_snapShot.getId());
        boolean can_analyze = (Duration.between(latest_snapShot.getCreatedAt(), Instant.now()).toHours() > time_between_analysis) ? true: false;
        if(!raw_signals.isPresent()){
            //Raw Signals Missing or unretreivable
            HealthScoreReport partialReport = new HealthScoreReport(
                latest_snapShot.getId(),
                pkg.getId(),
                can_analyze,
                latest_snapShot.getAggregate_score().doubleValue(),
                latest_snapShot.getActivity_score().doubleValue(),
                latest_snapShot.getMaintenance_score().doubleValue(),
                latest_snapShot.getAdoption_score().doubleValue(),
                latest_snapShot.getRisk_score().doubleValue(),
                null
            );
            return new PackageLookUpResult(LookupStatus.RAW_SIGNALS_MISSING, pkg, partialReport);
        }

        JsonNode raw_data = raw_signals.get().getRaw_data();
        HealthScoreReport fullReport =  new HealthScoreReport(
            latest_snapShot.getId(),
            pkg.getId(),
            can_analyze,
            latest_snapShot.getAggregate_score().doubleValue(),
            latest_snapShot.getActivity_score().doubleValue(),
            latest_snapShot.getMaintenance_score().doubleValue(),
            latest_snapShot.getAdoption_score().doubleValue(),
            latest_snapShot.getRisk_score().doubleValue(),
            raw_data
        );

        return new PackageLookUpResult(LookupStatus.REPORT_FOUND, pkg, fullReport);
    }

    public PackageAndSnapShotIDs writeToDB(
        String pkgName,
        EcosystemEntity ecosystem,
        HealthScoreReport generated_report, 
        UniversalPackageFeatures extracted_universal_signals, 
        RawNpmPayload raw_payload){

        //DB Entity Mapping----------------------------------------------------------------------
        Instant timeInstant = Instant.now();
        PackageHealthSnapShots_Entity snapShot = new PackageHealthSnapShots_Entity();
        snapShot.setAggregate_score(BigDecimal.valueOf(generated_report.getOverall()));
        snapShot.setActivity_score(BigDecimal.valueOf(generated_report.getActivity()));
        snapShot.setMaintenance_score(BigDecimal.valueOf(generated_report.getMaintenance()));
        snapShot.setAdoption_score(BigDecimal.valueOf(generated_report.getAdoption()));
        snapShot.setRisk_score(BigDecimal.valueOf(generated_report.getRisk()));
        snapShot.setCreatedAt(timeInstant);
        snapShot.setAlgorithm_v(System.getenv("ALGORITHM_VERSION"));

        UniversalSignals_Entity universal_signals = usE_Mapper.toEntity(extracted_universal_signals);
        universal_signals.setCalculated_at(timeInstant);
        
        
        RawSignals_Entity raw_signals = new RawSignals_Entity();
        raw_signals.setFetched_at(timeInstant);
        JsonNode ex = objectMapper.valueToTree(raw_payload);
        raw_signals.setRaw_data(ex);
        

        Optional<PackagesEntity> queried_pkg = pkgRepo.findByPackageNameAndEcosystem_id(pkgName, 1);
        PackagesEntity pkg;
        if(!queried_pkg.isPresent()){
            pkg = new PackagesEntity();
            pkg.setEcosystem(ecosystem);
            pkg.setPackageName(pkgName);
            pkg.setLatest_report(null);
        }
        else{
            pkg = queried_pkg.get();
        }

        pkg = pkgRepo.save(pkg);
        snapShot.setPkg(pkg);
        snapShot = pkgHealthRepo.save(snapShot);
        UUID report_id = snapShot.getId();
        UUID package_id = pkg.getId();

        universal_signals.setPkgHealthSnapShot(snapShot);
        raw_signals.setPkgHealthSnapShot(snapShot);

        uni_signals_repo.save(universal_signals);
        rawSignalsRepo.save(raw_signals);

        pkg.setLatest_report(snapShot);
        pkgRepo.save(pkg);
        
        return new PackageAndSnapShotIDs(report_id, package_id);
    }
}
