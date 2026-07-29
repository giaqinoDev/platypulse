package com.giosmdev.package_radar.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    private String errorCode;
    private String message;
    private int statusCode;
    private List<String> details;
    private List<String> suggestions;
    private String debugMessage;
}
