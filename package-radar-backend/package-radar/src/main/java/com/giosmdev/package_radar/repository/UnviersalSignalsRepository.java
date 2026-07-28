package com.giosmdev.package_radar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giosmdev.package_radar.model.DB_Entities.UniversalSignals_Entity;

import java.util.UUID;

public interface UnviersalSignalsRepository extends JpaRepository<UniversalSignals_Entity, UUID>{
    
    
}
