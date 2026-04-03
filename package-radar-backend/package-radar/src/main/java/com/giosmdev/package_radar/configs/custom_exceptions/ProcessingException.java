package com.giosmdev.package_radar.configs.custom_exceptions;

public class ProcessingException extends RuntimeException{
    private StackTraceElement[] stack_trace;

    public ProcessingException(){
        this.stack_trace = null;
    }

    public ProcessingException(StackTraceElement[] error){
        this.stack_trace = error;
    }

    public StackTraceElement[] getStack_Trace(){
        return stack_trace;
    }

    public void setStack_trace(StackTraceElement[] error){
        this.stack_trace = error;
    }
}
