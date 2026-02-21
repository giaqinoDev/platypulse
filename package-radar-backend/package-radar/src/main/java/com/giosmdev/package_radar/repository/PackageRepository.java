package com.giosmdev.package_radar.repository;

import org.springframework.stereotype.Repository;

import com.giosmdev.package_radar.model.Report;

@Repository
public class PackageRepository {

    public Report getReport(String pkgName){
        return new Report();
    }
    
}
