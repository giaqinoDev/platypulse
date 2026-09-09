package com.giosmdev.package_radar.dto;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private String report_id;
    private String message;             //User friendly error
    private T data;                     //Actual data (for success response)
    private ErrorDetails error;         //Error details (for error response)
    private LocalDateTime timestamp;    //Time that error occured
    private String origin;              //Origin of error (path, layer, ect.)

    public static <T> ApiResponse<T> success(String report_id, T data, String message){
        return ApiResponse.<T>builder()
                                    .success(true)
                                    .report_id(report_id)
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
