package com.giosmdev.package_radar.model.NPMRegistry.DownloadsTimeline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyDownloads{
    private int volume;
    private String start_date;
    private String end_date;
}