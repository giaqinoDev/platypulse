package com.giosmdev.package_radar.exceptions.ExternalAPIExceptions;

import org.springframework.http.HttpStatus;

import com.giosmdev.package_radar.exceptions.BaseException;

public class ExternalApiNotFoundException extends BaseException{

    public ExternalApiNotFoundException(String message, String route){
        super(
            message,
            "NOT_FOUND",
            HttpStatus.NOT_FOUND,
            route,
            null
        );
    }
}
