package com.giosmdev.package_radar.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration //class acts as a source of bean definitions ofr the IoC container
public class WebClientConfig {
    //Within a configuration class @Bean annotations are used to configure and instantiate beans

    @Bean //creates a bean of type WebClient, the beans name is "webClient"
    //tells SB to create an instance of WebClient and manage it in the IoC container
    public WebClient webClient(){
        return WebClient.builder().build(); //build without additional WebCLient configs
    }
    
}
