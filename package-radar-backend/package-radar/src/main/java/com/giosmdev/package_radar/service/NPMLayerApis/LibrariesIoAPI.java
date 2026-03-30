package com.giosmdev.package_radar.service.NPMLayerApis;

import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.configs.APIResponseLayer.ApiStatus;
import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.service.ApiErrorHandling.ApiErrorHandler;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class LibrariesIoAPI {
    private String BASE_LIBRARIES_URL = "https://libraries.io/api/{platform}/{package_name}?api_key=***REMOVED***";
    
    private WebClient webClient;
    private ApiErrorHandler apiErrorHandler;

    public LibrariesIoAPI(WebClient webClient, ApiErrorHandler apiErrorHandler){
        this.webClient = webClient;
        this.apiErrorHandler = apiErrorHandler;
    }

    public Mono<ApiStatus<LibrariesIOResponse>> fetch(String platform, String package_name){
        Mono<LibrariesIOResponse> package_info = webClient.get()
                        .uri(BASE_LIBRARIES_URL, platform, package_name)
                        .retrieve()
                        .onStatus(
                            status -> status.is4xxClientError(),
                            response -> Mono.error(new PackageNotFoundException())
                        )
                        .onStatus(
                            status -> status.is5xxServerError(),
                            response -> Mono.error(new RegistryException())
                        )
                        .bodyToMono(JsonNode.class)
                        .timeout(Duration.ofSeconds(3))
                        .map(response -> {
                            int dependent_packages_count = response.get("dependents_count").asInt();
                            int dependent_repos_count = response.get("dependent_repos_count").asInt();

                            return new LibrariesIOResponse(dependent_packages_count, dependent_repos_count);
                        });
        Mono<ApiStatus<LibrariesIOResponse>> wrapped_result = apiErrorHandler.handle(package_info);
        return wrapped_result;
    }
}
