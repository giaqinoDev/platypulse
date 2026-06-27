package com.giosmdev.package_radar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.giosmdev.package_radar.model.DB_Entities.EcosystemEntity;

public interface EcosystemRepository extends JpaRepository<EcosystemEntity, Integer>{
    
}
