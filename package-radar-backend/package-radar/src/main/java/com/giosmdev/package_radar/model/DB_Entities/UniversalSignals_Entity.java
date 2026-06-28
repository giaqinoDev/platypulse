package com.giosmdev.package_radar.model.DB_Entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "universal_signals")
public class UniversalSignals_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "health_score_id", nullable = false, unique = true)
    private PackageHealthSnapShots_Entity pkgHealthSnapShot;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculated_at;

    //Time Based-----------------------------------------
    @Column(name = "days_since_last_release")
    private Double days_since_last_release;

    @Column(name = "days_since_last_commit")
    private Double days_since_last_commit;

    @Column(name = "commits_last_90_days")
    private Double commits_last_90_days;

    @Column(name = "commits_last_30_days")
    private Double commits_last_30_days;

    //Releases------------------------------------------
    @Column(name = "releases_per_year")
    private Double releases_per_year;

    @Column(name = "total_releases")
    private Double total_releases;

    //Popularity----------------------------------------
    @Column(name = "downloads_per_week")
    private Double downloads_per_week;

    @Column(name = "stars")
    private Double stars;

    @Column(name = "forks")
    private Double forks;

    @Column(name = "dependent_repos")
    private Double dependent_repos;

    @Column(name = "dependent_packages")
    private Double dependent_packages;

    //Issues--------------------------------------------
    @Column(name = "open_issues")
    private Double open_issues;

    @Column(name = "closed_issues")
    private Double closed_issues;

    //Contributors--------------------------------------
    @Column(name = "contributors_count")
    private Double contributors_count;

    @Column(name = "commits_count")
    private Double commits_count;

    @Column(name = "top_contributor_commit_count")
    private Double top_contributor_commit_count;

    //Dependencies-------------------------------------
    @Column(name = "direct_dependencies_count")
    private Double direct_dependencies_count;

    @Column(name = "direct_deprecated_dependencies")
    private Double direct_deprecated_dependencies;

    @Column(name = "dependencies_depth")
    private Double dependencies_depth;

    //Repo Info----------------------------------------
    @Column(name = "repo_age_days")
    private Double repo_age_days;

    @Column(name = "is_archived")
    private Boolean is_archived;
}
