package com.giosmdev.package_radar.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.giosmdev.package_radar.model.DB_Entities.PackagesEntity;

public interface PackageRepository extends JpaRepository<PackagesEntity, UUID>{
    Optional<PackagesEntity> findByPackageNameAndEcosystem_id(String package_name, Integer eco_id);
}
