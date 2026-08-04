package com.giosmdev.package_radar.exceptions;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;

import com.giosmdev.package_radar.dto.ApiResponse;
import com.giosmdev.package_radar.dto.ErrorDetails;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiNotFoundException;
import com.giosmdev.package_radar.exceptions.ExternalAPIExceptions.ExternalApiRateLimitException;

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

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsuportedApiResponse(UnsupportedMediaTypeException exception, HttpServletRequest request){
        ErrorDetails error = ErrorDetails.builder()
                                .errorCode("UNSUPPORTED_MEDIA_TYPE")
                                .message("Unable to handle api response")
                                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                                .details(null)
                                .suggestions(null)
                                .debugMessage(null)
                                .build();
        ApiResponse<Void> response = ApiResponse.error(error, request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ExternalApiRateLimitException.class)
    public ResponseEntity<ApiResponse<Void>> handApiRateLimitException(ExternalApiRateLimitException exception, HttpServletRequest request){
        ErrorDetails error = ErrorDetails.builder()
                                .errorCode("TOO_MANY_REQUESTS")
                                .message("Unable to execute report generation")
                                .statusCode(HttpStatus.TOO_MANY_REQUESTS.value())
                                .details(null)
                                .suggestions(Arrays.asList("Please wait a few seconds before trying to generating a new report."))
                                .debugMessage(null)
                                .build();
        ApiResponse<Void> response = ApiResponse.error(error, request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
