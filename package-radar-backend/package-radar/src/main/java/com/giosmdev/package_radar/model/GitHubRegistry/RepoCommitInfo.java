package com.giosmdev.package_radar.model.GitHubRegistry;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepoCommitInfo {
    //commit timeline is not needed (its a helper field computed in api layer)
    //private List<Instant> commit_timeline;
    private Map<Instant, Integer> commit_timeline_volume;
    
    private long commits_last_30_days;
    private long commits_last_90_days;
}
