package com.giosmdev.package_radar.service;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.model.NPMRegistry.RawHealthSignals;

//Models

import com.giosmdev.package_radar.model.NPMRegistry.RawHealthSignals;

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

    public Mono<ResponseEntity<RawHealthSignals>> getReport(String pkgName){
        return rawNPMRegResponse.genRawNPMResponse(pkgName);
    }
}
