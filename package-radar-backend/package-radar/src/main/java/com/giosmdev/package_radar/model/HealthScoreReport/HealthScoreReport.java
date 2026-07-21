package com.giosmdev.package_radar.model.HealthScoreReport;

import com.giosmdev.package_radar.model.NPMRegistry.RawNpmPayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthScoreReport {
    private double overall;
    private double activity;
    private double maintenance;
    private double adoption;
    private double risk;

    private RawNpmPayload raw_signals;
}
