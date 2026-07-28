package com.giosmdev.package_radar.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giosmdev.package_radar.model.DB_Entities.RawSignals_Entity;

public interface RawSignalsRepository extends JpaRepository<RawSignals_Entity, UUID>{
    Optional<RawSignals_Entity> findBypkgHealthSnapShot_id(UUID snapshot_id);

}
