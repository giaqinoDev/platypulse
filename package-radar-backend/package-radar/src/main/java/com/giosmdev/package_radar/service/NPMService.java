package com.giosmdev.package_radar.service;
import org.springframework.stereotype.Service;

//Models

import com.giosmdev.package_radar.model.NPMRegistry.RawNpmResponse;

//Repositories
import com.giosmdev.package_radar.repository.RawNPMRegistryResponse.RawNPMRegistryResponse;

import reactor.core.publisher.Mono;


@Service
public class NPMService {
    private RawNPMRegistryResponse rawNPMRegResponse;

    //Spring expects all constructor injections to be done in one consturctor
    public NPMService(RawNPMRegistryResponse rawNPMRegResponse){
        this.rawNPMRegResponse = rawNPMRegResponse;
    }

    public Mono<RawNpmResponse> getReport(String pkgName){
        System.out.println(pkgName);
        return rawNPMRegResponse.fetchNPMRegistry(pkgName);
    }      
}
