package com.giosmdev.package_radar.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giosmdev.package_radar.model.DB_Entities.PackageHealthSnapShots_Entity;

public interface PackageHealthScoreRepository extends JpaRepository<PackageHealthSnapShots_Entity, UUID>{
    Optional<List<PackageHealthSnapShots_Entity>> findByPkg_IdOrderByCreatedAtDesc(UUID pkgId);
    // Optional<String> getPackage_NameById(UUID pkgID);

    Optional<PackageHealthSnapShots_Entity> findById(UUID report_id);
}
