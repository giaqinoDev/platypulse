package com.giosmdev.package_radar.service.NPMLayerApis;

import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.configs.APIResponseLayer.ApiStatus;
import com.giosmdev.package_radar.configs.custom_exceptions.PackageNotFoundException;
import com.giosmdev.package_radar.configs.custom_exceptions.ProcessingException;
import com.giosmdev.package_radar.configs.custom_exceptions.RegistryException;
import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.service.ApiErrorHandling.ApiErrorHandler;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class LibrariesIoAPI {
    //private String BASE_LIBRARIES_URL = "https://libraries.io/api/{platform}/{package_name}?api_key={API_KEY}";
    
    private WebClient webClient;
    private ApiErrorHandler apiErrorHandler;
    private final String API_KEY = System.getenv("LIBRARIES_IO_API_KEY");
    public LibrariesIoAPI(WebClient webClient, ApiErrorHandler apiErrorHandler){
        this.webClient = webClient;
        this.apiErrorHandler = apiErrorHandler;
    }

    public Mono<ApiStatus<LibrariesIOResponse>> fetch(String platform, String package_name){
        Mono<LibrariesIOResponse> package_info = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("libraries.io")
                            .path("/api/{platform}/{package_name}")
                            .queryParam("api_key", API_KEY)
                            .build(platform, package_name)
                        )
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
                            try{
                                int dependent_packages_count = response.get("dependents_count").asInt();
                                int dependent_repos_count = response.get("dependent_repos_count").asInt();

                                return new LibrariesIOResponse(dependent_packages_count, dependent_repos_count);
                            }
                            catch(Exception error){
                                throw new ProcessingException(error.getStackTrace());
                            }
                        });
        Mono<ApiStatus<LibrariesIOResponse>> wrapped_result = apiErrorHandler.handle(package_info);
        return wrapped_result;
    }
}
