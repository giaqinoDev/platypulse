package com.giosmdev.package_radar.model.GitHubRegistry;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepoCommitInfo {
    private List<Instant> commit_timeline;

    private long commits_last_30_days;
    private long commits_last_90_days;
}
