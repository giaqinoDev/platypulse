package com.giosmdev.package_radar.service.UniversalSignaling;


//Direct Signals-----------------------------
// days_since_last_commit
// downloads_per_week
// stars
// forks
// dependencies_depth

//Derived Signals-----------------------------
// open_issues, closed_issues combine to issue_resolution_score

//Bus Factor----------------------------------
// commits_count
// top_contributor_commit_count
// contributors_count
// (Produces bus_factor_score)

//Contributor Activity-------------------------
// contributors_count
// commits_last_90_days
// (Produces maintainer_activity_score)

//Release Stability----------------------------
// releases_per_year
// repo_age_days
// total_releases

//Penalty Signals (Subtracts from Health)------------------------------
// direct_deprecated_dependencies
// isArchived
// dependency_depth

public class UniversalSignalNormalizer {
    
    //Activity Section 25%
    public double scoreCommitFrequency(Double days){ // 40%
        if(days == null){
            return -1;
        }
        else if(days <= 7){
            return 100;
        }
        else if(days <= 30){
            return 85;
        }
        else if (days <= 90){
            return 60;
        }
        else if (days <= 180){
            return 30;
        }

        return 0;
    } 

    public double scoreCommitsLast30Days(Double freq){ // 35%
        if(freq == null){
            return -1;
        }

        double score = freq * 2;
        return Math.min(score, 100);
    }

    public double scoreReleaseFreq(Double days){ // 25%
        if(days == null){
            return -1;
        }
        else if(days <= 30){
            return 100;
        }
        else if(days <= 90){
            return 80;
        }
        else if(days <= 180){
            return 60;
        }
        else if(days <= 365){
            return 30;
        }

        return 0;
    }

    //Maintenance Signals 30%
    public double scoreIssuesRatio(Double closed, Double open){ //35%
        if(open == null || closed == null){
            return -1;
        }
        double total = open + closed;
        if(total == 0){
            return 100;
        }
        double ratio = closed/total;
        return ratio * 100;
    }

    public double busFactorSignal(Double totalCommits, Double top_contributor_commits){ //30%
        if(totalCommits == null ||top_contributor_commits == null){
            return -1;
        }
        if(totalCommits == 0){
            return 0;
        }
        double busFactor = top_contributor_commits/totalCommits;
        //Inverse normalization
        return (1 - busFactor) * 100;
    }

    public double scoreReleaseFreq(double releases_per_year){ //20%
        return 0;
    }

    public double scoreDepDepracated(Double direct_deprecated, Double direct_count){ // 15%
        if(direct_count == null || direct_deprecated == null){
            return -1;
        }
        if(direct_count == 0){
            return 100;
        }

        double ratio = direct_deprecated/direct_count;

        return (1- ratio) * 100;
    }

    //Adoption Signals 25% logarithmic normalization
    public double scoreDownloads(double downloads_per_week){ //50%
        double score = (Math.log10(downloads_per_week + 1)/7.0) * 100;
        return Math.min(score, 100);
    }

    public double scoreDependentsSignal(double dependent_repos, double dependent_packages){ //30%
        Double value = dependent_repos;
        if(value == 0.0){
            value = dependent_packages;
        }
        if(value == 0.0){
            return -1;
        }

        double score = (Math.log10(value + 1)/5.0) * 100;
        return Math.min(score, 100);
    }

    public double scoreStarsForks(Double stars, Double forks){ // 20%
        if(stars == null && forks == null){
            return -1;
        }
        double starsScore = 0;
        double forksScore = 0;

        if(stars != null){
            starsScore = (Math.log10(stars + 1)/5.0) * 100;
        }
        if(forks != null){
            starsScore = (Math.log10(forks + 1)/ 4.0) * 100;
        }

        return weightedAverage(
            new double[]{starsScore, forksScore},
            new double[]{0.7, 0.3}
        );
    }

    //Risk Signals 20%
    public double scoreRiskSignal(double dependencies_depth){
        //inverse normalization
        return 0;
    }

    public double scoreArchivedSignal(Boolean isArchived){
        if(isArchived == null){
            return -1;
        }
        if(isArchived){
            return 100;
        }
        return 0;
    }

    public double scoreDirectDepreciatedDependencies(double direct_deprecated_dependencies){
        return 0;
    }

    //Util Functions
    private double weightedAverage(
            double[] values,
            double[] weights
    ) {

        double weightedSum = 0;
        double totalWeight = 0;

        for(int i = 0; i < values.length; i++) {

            if(values[i] == -1) {
                continue;
            }

            weightedSum += values[i] * weights[i];
            totalWeight += weights[i];
        }

        if(totalWeight == 0) {
            return 0;
        }

        return weightedSum / totalWeight;
    }
}

