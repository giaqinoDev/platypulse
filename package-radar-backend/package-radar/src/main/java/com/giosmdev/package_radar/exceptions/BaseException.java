package com.giosmdev.package_radar.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String[] suggestions;

    public BaseException(String message, String errorCode, HttpStatus httpStatus, String[] suggestions){
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.suggestions = suggestions;
    }
    
}
