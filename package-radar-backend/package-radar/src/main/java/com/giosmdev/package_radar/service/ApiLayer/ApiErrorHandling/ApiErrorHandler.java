package com.giosmdev.package_radar.service.ApiLayer.ApiErrorHandling;

import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.configs.APIResponseLayer.ApiStatus;
import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.ProcessingException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;

import reactor.core.publisher.Mono;

@Service
public class ApiErrorHandler {
    public <T> Mono<ApiStatus<T>> handle(Mono<T> response){
        return response.map(data -> new ApiStatus<>(data, 200, HttpStatus.OK, true))
                        .onErrorResume(PackageNotFoundException.class, error ->
                            Mono.just(new ApiStatus<>(null, 404, HttpStatus.NOT_FOUND, false))
                        )
                        .onErrorResume(RegistryException.class, error ->
                            Mono.just(new ApiStatus<>(null, 503, HttpStatus.SERVICE_UNAVAILABLE, false))
                        )
                        .onErrorResume(TimeoutException.class, error ->
                            Mono.just(new ApiStatus<>(null, 504, HttpStatus.GATEWAY_TIMEOUT, false))
                        )
                        .onErrorResume(ProcessingException.class, error -> 
                            Mono.just(new ApiStatus<>(null, 500, error.getStack_Trace(), false))
                        )
                        .onErrorResume(error ->
                            {
                                error.printStackTrace();
                                return Mono.just(new ApiStatus<>(null, 500, HttpStatus.INTERNAL_SERVER_ERROR, false));
                            }
                        );
    }
}
