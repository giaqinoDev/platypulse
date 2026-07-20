package com.giosmdev.package_radar.service.DB_Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.model.DB_Entities.EcosystemEntity;
import com.giosmdev.package_radar.model.DB_Entities.PackageHealthSnapShots_Entity;
import com.giosmdev.package_radar.model.DB_Entities.PackagesEntity;
import com.giosmdev.package_radar.repository.EcosystemRepository;
import com.giosmdev.package_radar.repository.PackageHealthScoreRepository;
import com.giosmdev.package_radar.repository.PackageRepository;

@Service
public class PlatyPulse_DB_Service {

    private EcosystemRepository ecosystemRepo;
    private PackageHealthScoreRepository pkgHealthRepo;
    private PackageRepository pkgRepo;
    public PlatyPulse_DB_Service(EcosystemRepository ecosystemRepo, PackageRepository pkgRepo, PackageHealthScoreRepository pkgHealthRepo){
        this.pkgRepo = pkgRepo;
        this.ecosystemRepo = ecosystemRepo;
        this.pkgHealthRepo = pkgHealthRepo;
    }

    public List<EcosystemEntity> getAllEcosystems(){
        return ecosystemRepo.findAll();
    }

    public PackageHealthSnapShots_Entity getHealthScore(String pkgName){
        Optional<PackagesEntity> queriedPkg = pkgRepo.findByPackageNameAndEcosystem_id(pkgName, 0);
        if(!queriedPkg.isPresent()){
            return null;
        }
        PackagesEntity pkg = queriedPkg.get(); //gets the result of query
        PackageHealthSnapShots_Entity latestReport = pkg.getLatest_report();
        if(latestReport == null){
            return null;
        }
        return latestReport;
    }
}
