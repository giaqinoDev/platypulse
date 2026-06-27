package com.giosmdev.package_radar.service.DB_Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.model.DB_Entities.EcosystemEntity;
import com.giosmdev.package_radar.repository.EcosystemRepository;

@Service
public class PlatyPulse_DB_Service {

    private EcosystemRepository ecosystemRepo;
    public PlatyPulse_DB_Service(EcosystemRepository ecosystemRepo){
        this.ecosystemRepo = ecosystemRepo;
    }

    public List<EcosystemEntity> getAllEcosystems(){
        return ecosystemRepo.findAll();
    }
}
