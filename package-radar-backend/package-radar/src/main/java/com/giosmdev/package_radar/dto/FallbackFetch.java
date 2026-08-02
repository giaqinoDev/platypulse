package com.giosmdev.package_radar.dto;

import com.giosmdev.package_radar.exceptions.BaseException;

import lombok.Data;

@Data
public class FallbackFetch<T> {
    private final T data;
    //Class<? extends BaseException> handles sub classes as well
    private final Class<? extends BaseException> exception_class;
    private final String notes;

    public FallbackFetch(T data, Class<? extends BaseException> exception_class){
        this.data = data;
        this.exception_class = exception_class;
        this.notes = null;
    }

    public FallbackFetch(T data, Class<? extends BaseException> exception_class, String notes){
        this.data = data;
        this.exception_class = exception_class;
        this.notes = notes;
    }
}
