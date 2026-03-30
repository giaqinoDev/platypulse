package com.giosmdev.package_radar.configs.custom_exceptions;

public class RegistryException extends RuntimeException {
    private String errorMessage = "NPM registry erorr";

    public String getErrorMessage(){
        return errorMessage;
    }
}
