package com.giosmdev.package_radar.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class PackageAndSnapShotIDs {
    private UUID report_id;
    private UUID package_id;
}
