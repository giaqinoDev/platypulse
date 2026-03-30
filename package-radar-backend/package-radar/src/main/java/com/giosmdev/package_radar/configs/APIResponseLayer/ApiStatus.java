package com.giosmdev.package_radar.configs.APIResponseLayer;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiStatus<T> {
    private T data;
    private int status;
    private HttpStatus status_msg;
    private boolean success;
}
