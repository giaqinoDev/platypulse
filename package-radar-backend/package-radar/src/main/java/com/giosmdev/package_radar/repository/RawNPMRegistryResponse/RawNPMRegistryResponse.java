package com.giosmdev.package_radar.repository.RawNPMRegistryResponse;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.giosmdev.package_radar.configs.APIResponseLayer.ApiStatus;
import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.model.NPMRegistry.PackageInfo;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmResponse;
import com.giosmdev.package_radar.model.NPMRegistry.DownloadsTimeline.WeeklyDownloads;
import com.giosmdev.package_radar.service.NPMLayerApis.LibrariesIoAPI;
import com.giosmdev.package_radar.service.NPMLayerApis.NPMRegistryAPI;

import reactor.core.publisher.Mono;

@Component
public class RawNPMRegistryResponse {

    //API Calls
    private final NPMRegistryAPI npmRegistryAPI;
    private final LibrariesIoAPI librariesIOResponse;

    public RawNPMRegistryResponse(NPMRegistryAPI npmRegistryAPI, LibrariesIoAPI librariesIOResponse){
        this.npmRegistryAPI = npmRegistryAPI;
        this.librariesIOResponse = librariesIOResponse;
    }

    public Mono<ResponseEntity<RawNpmResponse>> genRawNPMResponse(String pkgName){
        //Call NPM registry call
        Mono<PackageInfo> npm_reg_response = npmRegistryAPI.getPackageInfo(pkgName);

        Mono<ApiStatus<List<WeeklyDownloads>>> npm_reg_dwnld_timeline = npmRegistryAPI.getDownloadsTimeline(pkgName);
        
        //Libraries 
        Mono<ApiStatus<LibrariesIOResponse>> libIO_response = librariesIOResponse.fetch("npm", pkgName);

        // Merge downloads timeline into response
        // if any of the arguements in Mono.zip are Mono.empty() then Mono.zip will return empty
        Mono<ResponseEntity<RawNpmResponse>> NPMResponse = npm_reg_response.flatMap(package_info -> Mono.zip(npm_reg_dwnld_timeline, libIO_response)
                                        .map(tuple -> {
                                            //System.out.println("npm_reg_response: " + package_info.getStatus());
                                            List<WeeklyDownloads> downloads_timeline = tuple.getT1().getData();
                                            System.out.println("downloads_timeline: " + tuple.getT1().getStatus());

                                            LibrariesIOResponse libIO_Response = tuple.getT2().getData();
                                            System.out.println("libIO_Response: " + tuple.getT2().getStatus());

                                            return ResponseEntity.ok(new RawNpmResponse(package_info, downloads_timeline, libIO_Response));
                                        })
        )
        .onErrorResume(PackageNotFoundException.class, error ->
                            Mono.just(ResponseEntity.status(404).build())
                        )
                        .onErrorResume(RegistryException.class, error ->
                            Mono.just(ResponseEntity.status(503).build())
                        )
                        .onErrorResume(TimeoutException.class, error ->
                            Mono.just(ResponseEntity.status(504).build())
                        )
                        .onErrorResume(error ->
                            Mono.just(ResponseEntity.status(500).build())
                        );
        return NPMResponse;
    }

}
