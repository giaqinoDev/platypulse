package com.giosmdev.package_radar.service.ApiLayer.ApiErrorHandling;
import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.dto.FallbackFetch;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiNotFoundException;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiRateLimitException;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiUnavailableException;

import reactor.core.publisher.Mono;

//Utilized to intercept exception from API calls and resolve them gracefully before being handled by global exception handler
@Service
public class ApiErrorInterceptor {
    public <T> Mono<FallbackFetch<T>> handle(Mono<T> response){
        return response.map(data -> new FallbackFetch<>(data, null))
                        .onErrorResume(ExternalApiNotFoundException.class, error ->
                            Mono.just(new FallbackFetch<>(null, ExternalApiNotFoundException.class))
                        )
                        .onErrorResume(ExternalApiRateLimitException.class, error ->
                            Mono.just(new FallbackFetch<>(null, ExternalApiRateLimitException.class))
                        )
                        .onErrorResume(ExternalApiUnavailableException.class, error ->
                            Mono.just(new FallbackFetch<>(null, ExternalApiUnavailableException.class))
                        )
                        .onErrorResume(error ->
                            {
                                error.printStackTrace();
                                return Mono.just(new FallbackFetch<>(null, null));
                            }
                        );
    }
}
