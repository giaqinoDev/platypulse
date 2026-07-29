package com.giosmdev.package_radar.exceptions;

import org.springframework.http.HttpStatus;

public class PackageNotFoundException extends BaseException{
    public PackageNotFoundException(String message, String[] suggestions){
        super(
            message,
            "PACKAGE_NOT_FOUND",
            HttpStatus.NOT_FOUND,
            suggestions
        );
    }
}
