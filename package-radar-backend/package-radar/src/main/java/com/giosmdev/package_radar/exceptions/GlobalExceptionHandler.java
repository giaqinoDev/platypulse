package com.giosmdev.package_radar.exceptions;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.giosmdev.package_radar.dto.ApiResponse;
import com.giosmdev.package_radar.dto.ErrorDetails;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice //Makes this class handle exceptions globally.
public class GlobalExceptionHandler {

    @Value("${app.debug:false}")
    private boolean debugMode;
    
    @ExceptionHandler(ExternalApiNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiNotFound(ExternalApiNotFoundException exception, HttpServletRequest request){
        ErrorDetails error = ErrorDetails.builder()
                            .errorCode("NOT_FOUND")
                            .message("Unable to fetch NPM package")
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .details(null)
                            .suggestions(Arrays.asList("Check search query"))
                            .debugMessage(null)
                            .build();
        
        ApiResponse<Void> response = ApiResponse.error(error, request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
