package com.giosmdev.package_radar.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String route;
    private final Throwable cause;
    public BaseException(String message, String errorCode, HttpStatus httpStatus, String route, Throwable cause){
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.route = route;
        this.cause = cause;
    }
    
}
