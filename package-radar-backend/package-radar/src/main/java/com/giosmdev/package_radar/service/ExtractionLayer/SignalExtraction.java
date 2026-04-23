package com.giosmdev.package_radar.service.ExtractionLayer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.model.GitHubRegistry.RawGitHubResponse;
import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.model.NPMRegistry.PackageInfo;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmPayload;
import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependenciesInfo;
import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependencyTree;
import com.giosmdev.package_radar.model.universal.UniversalPackageFeatures;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class SignalExtraction {
    private ObjectMapper objMapper;

    public SignalExtraction(ObjectMapper objMapper) {
        this.objMapper = objMapper;
    }

    public UniversalPackageFeatures extractSignals(RawNpmPayload payload) {
        // JsonNode payload_tree = objMapper.valueToTree(payload);
        // Time based:
        Double days_since_last_release; //
        Double days_since_last_commit; //
        Double commits_last_90_days; //
        Double commits_last_30_days; //

        // Releases:
        Double releases_per_year; // <---------------------------Look into
        Double total_releases;

        // Popularity
        Double downloads_per_week; // Optional <---------------------------Look into
        Double stars; //
        Double forks; //
        Double dependent_repos; //
        Double dependent_packages; //

        // Issues
        Double open_issues; //
        Double closed_issues; //

        // Contributors
        Double contributors_count;//
        Double commits_count; //
        Double top_contributor_commit_count; //

        // Dependencies
        Double direct_dependencies_count = 0.0; //
        Double direct_deprecated_dependnecies = 0.0; //
        Double dependencies_depth = 0.0; //

        // Repo Info
        Double repo_age_days; //
        Boolean isArchived; //

        PackageInfo pkg_info = payload.getPackage_info();
        RawGitHubResponse git_response = payload.getGit_hub_reg();
        LibrariesIOResponse libIo_response = payload.getDependents_count();

        if (pkg_info != null) {
            String latest_version = pkg_info.getLatest_version();
            Instant release_date = Instant.parse(pkg_info.getVersions_release_info().path(latest_version).asString());
            
            days_since_last_release = (double) ChronoUnit.DAYS.between(release_date, Instant.now());
            List<DependenciesInfo> dependencies = pkg_info.getLatest_version_info().getDependencies();
            DependencyTree dependency_tree = pkg_info.getLatest_version_info().getDependency_tree();
            if (dependencies != null) {
                direct_dependencies_count = (double) dependencies.size();
                direct_deprecated_dependnecies = (double) dependencies.stream().filter(dep -> !dep.getDepreciated())
                        .count();
            }
            if (dependency_tree != null) {
                dependencies_depth = (double) dependency_tree.getTree_depth();
            }

            releases_per_year = calcReleasesPerYear(pkg_info.getVersions_release_info(),release_date);
        }

        if (git_response != null) {
            days_since_last_commit = (double) git_response.getRepo_info().getDays_since_last_push();
            commits_last_30_days = (double) git_response.getCommit_info().getCommits_last_30_days();
            commits_last_90_days = (double) git_response.getCommit_info().getCommits_last_90_days();
            stars = (double) git_response.getRepo_info().getStars_count();
            forks = (double) git_response.getRepo_info().getForks_count();
            closed_issues = (double) git_response.getRepo_info().getClosed_issues_count();

            commits_count = (double) git_response.getContributors_info().getTotal_commits();
            top_contributor_commit_count = (double) git_response.getContributors_info().getTop_contributor_commits();
            contributors_count = (double) git_response.getContributors_info().getContributors_count();
            repo_age_days = (double) git_response.getRepo_info().getRepo_age();
            isArchived = git_response.getRepo_info().isArchived();

        }

        if (libIo_response != null) {
            dependent_repos = (double) libIo_response.getDependent_repos_count();
            dependent_packages = (double) libIo_response.getDependent_packages_count();
        }
        return null; // Temp code
    }

    private double calcReleasesPerYear(JsonNode v_release_info, Instant latest_v_release_date){
        Instant first_release_date = Instant.parse(v_release_info.path(3).toString());
        Double release_timeline = (double) ChronoUnit.DAYS.between(latest_v_release_date, first_release_date)/365;
        int total_releases = v_release_info.size();
        return total_releases/release_timeline;
    }
}
