package com.giosmdev.package_radar.configs.custom_exceptions;

public class PackageNotFoundException extends RuntimeException {
    private String errorMessage = "Could not find package";

    public String getErrorMessage(){
        return errorMessage;
    }
}
