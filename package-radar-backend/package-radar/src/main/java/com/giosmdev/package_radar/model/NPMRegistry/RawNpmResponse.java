package com.giosmdev.package_radar.model.NPMRegistry;

import java.util.List;

import com.giosmdev.package_radar.model.LibrariesIO.LibrariesIOResponse;
import com.giosmdev.package_radar.model.NPMRegistry.DownloadsTimeline.WeeklyDownloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawNpmResponse {

    private PackageInfo package_info;
    private List<WeeklyDownloads> downloads_timeline;
    private LibrariesIOResponse dependents_count;
}
