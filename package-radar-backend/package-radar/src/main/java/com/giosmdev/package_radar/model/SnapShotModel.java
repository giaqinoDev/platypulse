package com.giosmdev.package_radar.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnapShotModel{
    private UUID snap_shot_id;
    private BigDecimal overall_score;
    private Instant created_at;
}
