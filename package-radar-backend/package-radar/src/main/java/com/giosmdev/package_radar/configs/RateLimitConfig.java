package com.giosmdev.package_radar.configs;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bucket;

@Configuration
public class RateLimitConfig {
    @Bean
    public Bucket rateBucket(){
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(10).refillGreedy(10, Duration.ofSeconds(1))) //Small window for burst limiting
                .addLimit(limit -> limit.capacity(60).refillGreedy(60, Duration.ofMinutes(1))) //medium window
                .addLimit(limit -> limit.capacity(1000).refillGreedy(1000, Duration.ofHours(1)))
                .build();
    }
}
