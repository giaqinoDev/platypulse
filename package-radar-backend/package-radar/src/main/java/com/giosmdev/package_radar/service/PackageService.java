package com.giosmdev.package_radar.service;

import org.springframework.stereotype.Service;

//Models
import com.giosmdev.package_radar.model.Report;

//Repositories
import com.giosmdev.package_radar.repository.PackageRepository;
@Service
public class PackageService {
    private final PackageRepository packageRepo;

    public PackageService(PackageRepository packageRepo){
        this.packageRepo = packageRepo;
    }

    public Report getReport(String pkgName){
        return packageRepo.getReport(pkgName);
    }
}
