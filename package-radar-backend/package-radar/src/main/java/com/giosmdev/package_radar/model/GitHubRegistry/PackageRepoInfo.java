package com.giosmdev.package_radar.model.GitHubRegistry;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageRepoInfo {
    private int stars_count;
    private int forks_count;
    private boolean isArchived; 

    private Instant pushed_at;
    private Instant created_at;
    private Instant updated_at;

    private Integer closed_issues_count;
    private Integer open_issues_count;
    
    //Derived Signals
    private long repo_age;
    private long days_since_last_push;
}
