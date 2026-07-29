package com.giosmdev.package_radar.model.ExceptionHandling;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;            //True for success, false for error
    private String message;             //User friendly error
    private T data;                     //Actual data (for success response)
    private ErrorDetails error;         //Error details (for error response)
    private LocalDateTime timestamp;    //Time that error occured
    private String origin;              //Origin of error (path, layer, ect.)

    public static <T> ApiResponse<T> success(T data, String message){
        return ApiResponse.<T>builder()
                                    .success(true)
                                    .message(message)
                                    .data(data)
                                    .timestamp(LocalDateTime.now())
                                    .build();
    }

    public static <T> ApiResponse<T> error(ErrorDetails error, String origin){
        return ApiResponse.<T>builder()
                                    .success(false)
                                    .error(error)
                                    .origin(origin)
                                    .timestamp(LocalDateTime.now())
                                    .build();
    }
}
