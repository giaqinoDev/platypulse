package com.giosmdev.package_radar.service.ApiLayer.NPMLayerApis;

import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.dto.FallbackFetch;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiNotFoundException;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiRateLimitException;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiUnavailableException;
import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.service.ApiLayer.ApiErrorHandling.ApiErrorInterceptor;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class LibrariesIoAPI {
    //private String BASE_LIBRARIES_URL = "https://libraries.io/api/{platform}/{package_name}?api_key={API_KEY}";
    
    private WebClient webClient;
    private ApiErrorInterceptor apiErrorInterceptor;
    private final String API_KEY = System.getenv("LIBRARIES_IO_API_KEY");
    public LibrariesIoAPI(WebClient webClient, ApiErrorInterceptor apiErrorInterceptor){
        this.webClient = webClient;
        this.apiErrorInterceptor = apiErrorInterceptor;
    }

    public Mono<FallbackFetch<LibrariesIOResponse>> fetch(String platform, String package_name){
        String route = "https/librares.io/api/" + platform + "/" + package_name;
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
                                status -> status.value() == 404,
                                response -> Mono.error(new ExternalApiNotFoundException("Package not found in LibrariesIO registry", route))
                        )
                        .onStatus(
                            status -> status.value() == 429,
                            response -> Mono.error(new ExternalApiRateLimitException("LibrariesIO registry rate limit exceeded", route))
                        )
                        .onStatus(
                            status -> status.is5xxServerError(),
                            response -> {
                                System.out.println("500 Level Error");
                                return Mono.error(new ExternalApiUnavailableException("LibrariesIO Registry unavailable", route));
                            }
                        )
                        .bodyToMono(JsonNode.class)
                        .timeout(Duration.ofSeconds(10))
                        .map(response -> {
                            int dependent_packages_count = response.get("dependents_count").asInt();
                            int dependent_repos_count = response.get("dependent_repos_count").asInt();

                            return new LibrariesIOResponse(dependent_packages_count, dependent_repos_count);
                        });
        Mono<FallbackFetch<LibrariesIOResponse>> wrapped_result = apiErrorInterceptor.handle(package_info);
        return wrapped_result;
    }
}
