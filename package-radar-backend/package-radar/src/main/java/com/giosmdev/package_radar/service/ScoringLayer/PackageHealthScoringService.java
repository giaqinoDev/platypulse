package com.giosmdev.package_radar.service.ScoringLayer;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.model.HealthScoreReport.HealthScoreReport;
import com.giosmdev.package_radar.model.universal.UniversalPackageFeatures;

@Service
public class PackageHealthScoringService {

    public HealthScoreReport score(UniversalPackageFeatures features){

        double activity = calculateActivityScore(features);
        double maintenance = calculateMaintenanceScore(features);
        double adoption = calculateAdoptionScore(features);
        double risk =calculateRiskScore(features);

        //Overall signal categorization weights
        double overall = weightedAverage(
            new double[]{activity, maintenance, adoption, risk},
            new double[]{0.15, 0.30, 0.35, 0.20}
        );
        
        System.out.println(maintenance);
        return new HealthScoreReport(
            round(overall),
            round(activity),
            round(maintenance),
            round(adoption),
            round(risk)
        );
    }

     
    //activity scoring
    private double calculateActivityScore(UniversalPackageFeatures features){
        double commitRecency = scoreCommitRecency(features.getDays_since_last_commit());
        double releaseRecency = scoreReleaseRecency(features.getDays_since_last_release());
        double commitVolume = scoreCommitVolume(features.getCommits_last_30_days());

        //Sub category weighting
        return weightedAverage(
            new double[]{commitRecency, releaseRecency, commitVolume},
            new double[]{0.40, 0.35, 0.25}
        );
    }

    //maintenance scoring
    private double calculateMaintenanceScore(UniversalPackageFeatures features){

        double issueResponsiveness = scoreIssueResponsiveness(features.getOpen_issues(), features.getClosed_issues());
        double busFactor = scoreBusFactor(features.getCommits_count(), features.getTop_contributor_commit_count());

        double releaseRecency = scoreReleaseRecency(features.getDays_since_last_release());
        double releaseCadence = scoreReleaseCadence(features.getReleases_per_year(), releaseRecency);

        System.out.println("issueResponsiveness: " + issueResponsiveness);
        System.out.println("busFactor: " + busFactor);
        System.out.println("releaseCadence: " + releaseCadence);
        //Sub Category weighting
        return weightedAverage(
            new double[]{issueResponsiveness, busFactor, releaseCadence},
            new double[]{0.40, 0.35, 0.25}
        );
    }

    //Adoption scoring
    private double calculateAdoptionScore(UniversalPackageFeatures features){

        double downloads = scoreDownloads(features.getDownloads_per_week());
        double dependents = scoreDependents(features.getDependent_repos(), features.getDependent_packages());
        double community = scoreCommunity(features.getStars(), features.getForks());

        //Sub category weighting
        return weightedAverage(
            new double[]{downloads, dependents, community},
            new double[]{0.50, 0.30, 0.20}
        );
    }

    //Risk scoring
    private double calculateRiskScore(UniversalPackageFeatures features){

        double archived = scoreArchived(features.getIsArchived());
        double dependencyDepth = scoreDependencyDepth(features.getDependencies_depth());
        double deprecatedDependencies = scoreDeprecatedDependencies(features.getDirect_deprecated_dependnecies(), features.getDirect_dependencies_count());

        //Sub category weighting
        return weightedAverage(
            new double[]{archived, dependencyDepth, deprecatedDependencies},
            new double[]{0.40, 0.35, 0.25}
        );
    }


//Signal Scoring ---------------------------------------------------------------------------------------------------------------------
    private double scoreCommitRecency(Double days){
        if(days == null){
            return -1;
        }
        if(days <= 7){
            return 100;
        }
        if(days <= 30){
            return 85;
        }
        if(days <= 90){
            return 60;
        }
        if(days <= 180){
            return 30;
        }
        return 0;
    }

    private double scoreReleaseRecency(Double days){

        if(days == null){
            return -1;
        }
        if(days <= 30){
            return 100;
        }
        if(days <= 90){
            return 80;
        }
        if(days <= 180){
            return 60;
        }
        if(days <= 365){
            return 30;
        }
        return 0;
    }

    private double scoreCommitVolume(Double commits){

        if(commits == null){
            return -1;
        }

        double score = commits * 2;

        return Math.min(score, 100);
    }

    private double scoreIssueResponsiveness(Double open, Double closed){

        if(open == null || closed == null) {
            return -1;
        }

        double total = open + closed;

        if(total == 0) {
            return 100;
        }

        double ratio = closed / total;

        return ratio * 100;
    }

    private double scoreBusFactor(Double totalCommits, Double topContributorCommits){

        if(totalCommits == null || topContributorCommits == null) {
            return -1;
        }

        if(totalCommits == 0) {
            return 0;
        }

        double dominance = topContributorCommits / totalCommits;

        return (1 - dominance) * 100;
    }

    private double scoreReleaseCadence(Double releasesPerYear, Double releaseRecency){

        if(releasesPerYear == null) {
            return -1;
        }

        double recency_score = scoreReleaseRecency(releaseRecency);
        double score = (releasesPerYear * 10)/(recency_score * 0.1);

        return Math.min(score, 100);
    }

    private double scoreDownloads(Double downloads){

        System.out.println(downloads);
        if(downloads == null) {
            return -1;
        }

        double score = (Math.log10(downloads + 1) / 7.0) * 100;
        return Math.min(score, 100);
    }

    private double scoreDependents(Double repos, Double packages){

        Double value = repos;

        if(value == null) {
            value = packages;
        }

        if(value == null) {
            return -1;
        }

        double score = (Math.log10(value + 1) / 5.0) * 100;

        return Math.min(score, 100);
    }

    private double scoreCommunity(Double stars, Double forks){

        if(stars == null && forks == null) {
            return -1;
        }

        double starsScore = 0;
        double forksScore = 0;

        if(stars != null) {
            starsScore = (Math.log10(stars + 1) / 5.0) * 100;
        }

        if(forks != null) {
            forksScore = (Math.log10(forks + 1) / 4.0) * 100;
        }

        return weightedAverage(
            new double[]{
                starsScore,
                forksScore
            },
            new double[]{
                0.70,
                0.30
            }
        );
    }

    private double scoreDependencyDepth(Double depth){

        if(depth == null) {
            return -1;
        }

        double score = 100 - (depth * 10);
        return Math.max(score, 0);
    }

    private double scoreDeprecatedDependencies(Double deprecated, Double total){

        if(deprecated == null || total == null) {
            return -1;
        }

        if(total == 0) {
            return 100;
        }

        double ratio = deprecated / total;
        return (1 - ratio) * 100;
    }

    //Archived repositories are risky
    private double scoreArchived(Boolean archived){

        if(archived == null) {
            return -1;
        }

        return archived ? 0 : 100;
    }

//Utility Functions -----------------------------------------------------------------------------------------------------------------------------------------
    private double weightedAverage(double[] values, double[] weights){

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

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
