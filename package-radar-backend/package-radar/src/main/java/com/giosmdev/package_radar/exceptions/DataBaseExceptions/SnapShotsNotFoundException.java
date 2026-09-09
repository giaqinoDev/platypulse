package com.giosmdev.package_radar.exceptions.DataBaseExceptions;

import org.springframework.http.HttpStatus;

import com.giosmdev.package_radar.exceptions.BaseException;

public class SnapShotsNotFoundException extends BaseException{
    public SnapShotsNotFoundException(String message, String route){
        super(
            message,
            "NOT_FOUND",
            HttpStatus.NOT_FOUND,
            route,
            null
        );
    }
}
