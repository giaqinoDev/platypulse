package com.giosmdev.package_radar.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data //Lombok annotation that generates getters and setters
@AllArgsConstructor //automatically generates a constructor with one arguement for every field in the class
public class Report {
    private String name;
    private String version;
    private String description;
    
    //private Map<String, String> releases;
}
