package com.giosmdev.package_radar.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data //Lombok annotation that generates getters and setters
@AllArgsConstructor //automatically generates a constructor with one arguement for every field in the class
public class Report {
    private String packageName;
    private String latest_version;
    private String latest_release_date;
    
    private Map<String, String> releases;

    public Report(){
        packageName = "ahhh";
        latest_version = "fefef";
        latest_release_date = "wdqwd";
        releases = null;
    }
}
