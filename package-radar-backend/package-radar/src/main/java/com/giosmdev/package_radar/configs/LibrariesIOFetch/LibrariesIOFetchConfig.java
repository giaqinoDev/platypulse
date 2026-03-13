package com.giosmdev.package_radar.configs.LibrariesIOFetch;

import java.util.function.BiFunction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Configuration
public class LibrariesIOFetchConfig {
    private String BASE_LIBRARIES_URL = "https://libraries.io/api/{platform}/{package_name}?api_key=***REMOVED***";
    private WebClient webClient;

    public LibrariesIOFetchConfig(WebClient webClient){
        this.webClient = webClient;
    }

    @Bean
    public BiFunction<String, String, Mono<LibrariesIOResponse>> librariesIOFetch(){
        return (String platform, String package_name) -> {
                    Mono<LibrariesIOResponse> package_info = webClient.get()
                        .uri(BASE_LIBRARIES_URL, platform, package_name)
                        .retrieve()
                        .onStatus(
                            status -> status.is4xxClientError(),
                            response -> Mono.error(new RuntimeException("Package not found"))
                        )
                        .onStatus(
                            status -> status.is5xxServerError(),
                            response -> Mono.error(new RuntimeException("Libraries IO error"))
                        )
                        .bodyToMono(JsonNode.class)
                        .map(response -> {
                            int dependent_packages_count = response.get("dependents_count").asInt();
                            int dependent_repos_count = response.get("dependent_repos_count").asInt();

                            return new LibrariesIOResponse(dependent_packages_count, dependent_repos_count);
                        })
                        .onErrorResume(error -> {
                            
                            if(error.getMessage().contains("Package not found")){
                                //Place holder
                                return Mono.just(new LibrariesIOResponse());
                            }
                            else if(error.getMessage().contains("NPM registry error")){
                                return Mono.just(new LibrariesIOResponse());
                            }
                            else{
                                return Mono.just(new LibrariesIOResponse());
                            }
                        });
                    
                    
                    return package_info;
                };
    }
}
