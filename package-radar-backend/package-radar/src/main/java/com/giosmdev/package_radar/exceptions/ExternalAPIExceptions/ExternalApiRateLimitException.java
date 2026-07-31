package com.giosmdev.package_radar.exceptions.ExternalAPIExceptions;

import org.springframework.http.HttpStatus;

import com.giosmdev.package_radar.exceptions.BaseException;

public class ExternalApiRateLimitException extends BaseException{
    public ExternalApiRateLimitException(String message, String route){
        super(
            message,
            "TOO_MANY_REQUESTS",
            HttpStatus.TOO_MANY_REQUESTS,
            route,
            null
        );
    }
}
