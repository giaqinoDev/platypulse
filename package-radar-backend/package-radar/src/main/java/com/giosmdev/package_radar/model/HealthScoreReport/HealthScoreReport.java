package com.giosmdev.package_radar.model.HealthScoreReport;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthScoreReport {
    private UUID report_id;
    private double overall;
    private double activity;
    private double maintenance;
    private double adoption;
    private double risk;

    private JsonNode raw_signals;
}
