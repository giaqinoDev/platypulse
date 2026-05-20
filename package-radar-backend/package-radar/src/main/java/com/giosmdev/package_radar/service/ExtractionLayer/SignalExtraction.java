package com.giosmdev.package_radar.service.ExtractionLayer;

//import static org.junit.jupiter.api.DynamicTest.stream;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.model.GitHubRegistry.RawGitHubResponse;
import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.model.NPMRegistry.PackageInfo;
import com.giosmdev.package_radar.model.NPMRegistry.RawNpmPayload;
import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependenciesInfo;
import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependencyTree;
import com.giosmdev.package_radar.model.NPMRegistry.DownloadsTimeline.WeeklyDownloads;
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
        Double days_since_last_release = null; //
        Double days_since_last_commit = null; //
        Double commits_last_90_days = null; //
        Double commits_last_30_days = null; //

        // Releases:
        Double releases_per_year = null; // <---------------------------Look into
        Double total_releases = null;

        // Popularity
        Double downloads_per_week = null; // Optional <---------------------------Look into
        Double stars = null; //
        Double forks = null; //
        Double dependent_repos = null; //
        Double dependent_packages = null; //

        // Issues
        Double open_issues = null; //
        Double closed_issues = null; //

        // Contributors
        Double contributors_count = null;//
        Double commits_count = null; //
        Double top_contributor_commit_count = null; //

        // Dependencies
        Double direct_dependencies_count = 0.0; //
        Double direct_deprecated_dependencies = 0.0; //
        Double dependencies_depth = 0.0; //

        // Repo Info
        Double repo_age_days = null; //
        Boolean isArchived = false; //

        PackageInfo pkg_info = payload.getPackage_info();
        RawGitHubResponse git_response = payload.getGit_hub_reg();
        LibrariesIOResponse libIo_response = payload.getDependents_count();
        List<WeeklyDownloads> downloads_timeline = payload.getDownloads_timeline();

        if (pkg_info != null) {
            String latest_version = pkg_info.getLatest_version();
            Instant release_date = Instant.parse(pkg_info.getVersions_release_info().path(latest_version).asString());
            
            days_since_last_release = (double) ChronoUnit.DAYS.between(release_date, Instant.now());
            List<DependenciesInfo> dependencies = pkg_info.getLatest_version_info().getDependencies();
            DependencyTree dependency_tree = pkg_info.getLatest_version_info().getDependency_tree();
            if (dependencies != null) {
                direct_dependencies_count = (double) dependencies.size();
                direct_deprecated_dependencies = (double) dependencies.stream().filter(dep -> dep.getDepreciated() == true)
                        .count();
            }
            if (dependency_tree != null) {
                dependencies_depth = (double) dependency_tree.getTree_depth();
            }
            total_releases = (double) pkg_info.getVersions_release_info().size() - 2;
            releases_per_year = calcReleasesPerYear(pkg_info.getVersions_release_info(), release_date);
        }
        if(downloads_timeline != null){
            downloads_per_week = calcDownloadsPerWeek(downloads_timeline);
        }
        if (git_response != null) {
            days_since_last_commit = (double) git_response.getRepo_info().getDays_since_last_push();
            commits_last_30_days = (double) git_response.getCommit_info().getCommits_last_30_days();
            commits_last_90_days = (double) git_response.getCommit_info().getCommits_last_90_days();
            stars = (double) git_response.getRepo_info().getStars_count();
            forks = (double) git_response.getRepo_info().getForks_count();
            open_issues = (double) git_response.getRepo_info().getOpen_issues_count();
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

        return new UniversalPackageFeatures(
            days_since_last_release,
            days_since_last_commit,
            commits_last_90_days,
            commits_last_30_days,
            releases_per_year,
            total_releases,
            downloads_per_week,
            stars,
            forks,
            dependent_repos,
            dependent_packages,
            open_issues,
            closed_issues,
            contributors_count,
            commits_count,
            top_contributor_commit_count,
            direct_dependencies_count,
            direct_deprecated_dependencies,
            dependencies_depth,
            repo_age_days,
            isArchived
        );
    }

    //--------------------------------------------Helper Functions-------------------------------------------------------------------------------------------------
    private double calcReleasesPerYear(JsonNode v_release_info, Instant latest_v_release_date){
        //List<String> dicToList = ArrayList<>(v_release_info)
        Instant first_release_date = Instant.parse(v_release_info.path("created").asString());
        Double release_timeline = (double) ChronoUnit.DAYS.between(first_release_date, latest_v_release_date)/365;

        int total_releases_count = v_release_info.size();

        System.out.println("first_release_date: " + first_release_date);
        System.out.println("latest_v_release_date: " + latest_v_release_date);
        System.out.println("release_timeline: " + release_timeline);
        System.out.println("total_releases: " + total_releases_count);

        return total_releases_count/release_timeline;
    }

    private double calcDownloadsPerWeek(List<WeeklyDownloads> downloads_timeline){
        int timeline_length = downloads_timeline.size();
        //int total_downloads;
        //Trim spikes over the last 8 weeks
        List<WeeklyDownloads> eight_week_timeline = downloads_timeline.subList(timeline_length - 9, timeline_length-1);
        eight_week_timeline.stream().sorted(Comparator.comparing(WeeklyDownloads::getVolume)).collect(Collectors.toList());
        eight_week_timeline.remove(0);
        eight_week_timeline.remove(eight_week_timeline.size()-1);

        double total_downloads = eight_week_timeline.stream().mapToInt(wkDwnld -> wkDwnld.getVolume()).sum();
        //List<Integer> sub_downloads_timeline = eight_week_timeline.stream().map(wkDwnld -> wkDwnld.getVolume()).toList();
        //sub_downloads_timeline.forEach(volume -> {total_downloads + volume});
        return total_downloads/8.0;
    }
}
