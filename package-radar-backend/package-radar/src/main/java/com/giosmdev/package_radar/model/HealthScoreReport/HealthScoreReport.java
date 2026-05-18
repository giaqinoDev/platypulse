package com.giosmdev.package_radar.model.HealthScoreReport;

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
}
