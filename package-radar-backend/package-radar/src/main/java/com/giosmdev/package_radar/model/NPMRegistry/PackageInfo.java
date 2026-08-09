package com.giosmdev.package_radar.model.NPMRegistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageInfo {
    private String pkge_name;
    private String description;
    private String latest_version;
    private LatestVersionInfo latest_version_info;
    private JsonNode versions_release_info;
}
