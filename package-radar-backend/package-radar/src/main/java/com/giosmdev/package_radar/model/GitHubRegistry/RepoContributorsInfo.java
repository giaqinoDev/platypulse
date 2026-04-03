package com.giosmdev.package_radar.model.GitHubRegistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepoContributorsInfo {
    private int total_commits;
    private int top_contributor_commits;
    private double top_commits_ratio;
}
