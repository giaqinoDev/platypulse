package com.giosmdev.package_radar.configs;

import java.net.http.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration //class acts as a source of bean definitions ofr the IoC container
public class WebClientConfig {
    //Within a configuration class @Bean annotations are used to configure and instantiate beans

    //Defines the configs of codecs (http message readers and writers) for 
    //WebClient to serialize and deserialize request and responses bodies
    ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(100 * 1024 * 1024)) // 20MB
                .build();
    @Bean //creates a bean of type WebClient, the beans name is "webClient"
    //tells SB to create an instance of WebClient and manage it in the IoC container
    public WebClient webClient(){
        return WebClient.builder().exchangeStrategies(strategies).build(); //build without additional WebCLient configs
    }
    
}
