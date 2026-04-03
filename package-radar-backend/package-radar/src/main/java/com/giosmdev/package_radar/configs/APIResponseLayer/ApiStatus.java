package com.giosmdev.package_radar.configs.APIResponseLayer;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ApiStatus<T> {
    private T data;
    private int status;
    private HttpStatus status_msg;
    private StackTraceElement[] status_msg_string;
    private boolean success;

    public ApiStatus(T data, int status, HttpStatus status_msg, boolean success){
        this.data = data;
        this.status = status;
        this.status_msg = status_msg;
        this.success = success;
    }

    public ApiStatus(T data, int status, StackTraceElement[] status_msg_string, boolean success){
        this.data = data;
        this.status = status;
        this.status_msg_string = status_msg_string;
        this.success = success;
    }
}
