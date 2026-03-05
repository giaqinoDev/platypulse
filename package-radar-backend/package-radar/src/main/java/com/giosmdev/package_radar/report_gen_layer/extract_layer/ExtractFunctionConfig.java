package com.giosmdev.package_radar.report_gen_layer.extract_layer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

@Configuration
public class ExtractFunctionConfig {
    @Bean
    public ReportContainer extractNPMData(){

        return new ReportContainer() {
            @Override
            public Object extract(Object rawResponse){
                return null;
            }
        };
    }
}

