package com.giosmdev.package_radar.model.GitHubRegistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawGitHubResponse {
    private PackageRepoInfo repo_info;
    private RepoCommitInfo commit_info;
}
