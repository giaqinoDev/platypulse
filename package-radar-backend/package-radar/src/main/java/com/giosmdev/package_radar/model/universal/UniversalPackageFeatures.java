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
    private Double commits_last_90_days;
    private Double commits_last_30_days;

    //Releases:
    private Double releases_per_year;
    private Double total_releases; //optional

    //Popularity
    private Double downloads_per_week; //optional
    private Double stars;
    private Double forks;
    private Double dependent_repos; //preferred
    private Double dependent_packages; //fall back

    //Issues
    private Double open_issues;
    private Double closed_issues;

    //Contributors
    private Double contributors_count;
    private Double commits_count;
    private Double top_contributor_commit_ratio;

    //Dependencies
    private Double direct_dependencies_count;
    private Double direct_deprecated_dependnecies;
    private Double dependencies_depth;

    //Repo Info
    private Double repo_age_days;
    private Boolean isArchived;
}