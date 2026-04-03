package com.giosmdev.package_radar.configs.custom_exceptions;

public class ProcessingException extends RuntimeException{
    private String stack_trace;

    public String getStack_Trace(){
        return stack_trace;
    }

    public void setStack_trace(String error){
        this.stack_trace = error;
    }
}
