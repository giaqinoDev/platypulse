package com.giosmdev.package_radar.exceptions.DataBaseExceptions;

import org.springframework.http.HttpStatus;

import com.giosmdev.package_radar.exceptions.BaseException;

public class EcosystemNotFoundException extends BaseException{
    public EcosystemNotFoundException(String message, String route){
        super(
            message,
            "NOT_FOUND",
            HttpStatus.NOT_FOUND,
            route,
            null
        );
    }
}
