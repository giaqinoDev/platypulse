package com.giosmdev.package_radar.service.DB_Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.model.DB_Entities.EcosystemEntity;
import com.giosmdev.package_radar.model.DB_Entities.PackageHealthSnapShots_Entity;
import com.giosmdev.package_radar.model.DB_Entities.PackagesEntity;
import com.giosmdev.package_radar.model.DB_Entities.RawSignals_Entity;
import com.giosmdev.package_radar.model.HealthScoreReport.HealthScoreReport;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmPayload;
import com.giosmdev.package_radar.repository.EcosystemRepository;
import com.giosmdev.package_radar.repository.PackageHealthScoreRepository;
import com.giosmdev.package_radar.repository.PackageRepository;
import com.giosmdev.package_radar.repository.RawSignalsRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class PlatyPulse_DB_Service {

    private EcosystemRepository ecosystemRepo;
    private PackageHealthScoreRepository pkgHealthRepo;
    private RawSignalsRepository rawSignalsRepo;
    private PackageRepository pkgRepo;
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
    public PlatyPulse_DB_Service(EcosystemRepository ecosystemRepo, PackageRepository pkgRepo, PackageHealthScoreRepository pkgHealthRepo, RawSignalsRepository rawSignalsRepo){
        this.pkgRepo = pkgRepo;
        this.ecosystemRepo = ecosystemRepo;
        this.pkgHealthRepo = pkgHealthRepo;
        this.rawSignalsRepo = rawSignalsRepo;
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
        if(!raw_signals.isPresent()){
            //Raw Signals Missing or unretreivable
            HealthScoreReport partialReport = new HealthScoreReport(
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
            latest_snapShot.getAggregate_score().doubleValue(),
            latest_snapShot.getActivity_score().doubleValue(),
            latest_snapShot.getMaintenance_score().doubleValue(),
            latest_snapShot.getAdoption_score().doubleValue(),
            latest_snapShot.getRisk_score().doubleValue(),
            raw_data
        );

        return new PackageLookUpResult(LookupStatus.REPORT_FOUND, pkg, fullReport);
    }
}
