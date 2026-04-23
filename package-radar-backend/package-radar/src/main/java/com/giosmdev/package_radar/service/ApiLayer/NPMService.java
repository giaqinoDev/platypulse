package com.giosmdev.package_radar.service.ApiLayer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmPayload;
import com.giosmdev.package_radar.model.universal.UniversalPackageFeatures;
//Repositories
import com.giosmdev.package_radar.repository.RawNPMRegistryResponse.RawNPMRegistryResponse;
import com.giosmdev.package_radar.service.ExtractionLayer.SignalExtraction;

import reactor.core.publisher.Mono;


@Service
public class NPMService {
    private RawNPMRegistryResponse rawNPMRegResponse;
    private SignalExtraction signalExtractor;

    //Spring expects all constructor injections to be done in one consturctor
    public NPMService(RawNPMRegistryResponse rawNPMRegResponse, SignalExtraction signalExtractor){
        this.rawNPMRegResponse = rawNPMRegResponse;
        this.signalExtractor = signalExtractor;
    }

    public Mono<ResponseEntity<RawNpmPayload>> getReport(String pkgName){
        Mono<ResponseEntity<RawNpmPayload>> raw_response =  rawNPMRegResponse.genRawNPMResponse(pkgName);
        Mono<ResponseEntity<UniversalPackageFeatures>> extracted_signals = raw_response.map((response) -> {
            return ResponseEntity.ok(signalExtractor.extractSignals(response.getBody()));
        });
        return raw_response;
    }
}
