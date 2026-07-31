package com.giosmdev.package_radar.exceptions.ExternalAPIExceptions;

import org.springframework.http.HttpStatus;

import com.giosmdev.package_radar.exceptions.BaseException;

public class ExternalApiUnavailableException extends BaseException {
    public ExternalApiUnavailableException(String message, String route){
        super(
            message,
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR,
            route,
            null
        );
    }
}
