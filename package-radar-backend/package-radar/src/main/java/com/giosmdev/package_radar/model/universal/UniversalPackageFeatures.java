package com.giosmdev.package_radar.model.universal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UniversalPackageFeatures{
    //Time based:
    private Double days_since_last_release;
    private Double days_since_last_commit;
    private Double commit_last_90_days;
    private Double commit_last_30_days;

    //Releases:
    private Double releases_per_year;

    //Popularity
    private Double downloads_per_week;
    private Double stars;
    private Double forks;
    private Double dependents;

    //Issues
    private Double open_issues;
    private Double closed_issues;
    private Double issue_close_rate;

    //Contributors
    private Double contributors_count;
    private Double top_contributors_ratio;

    //Dependencies
    private Double dependencies_count;
    private Double dependencies_depth;
    private Double depreciated_dependency_ratio;

    //Repo Info
    private Double repo_age_days;
    private Double isArchived;
}