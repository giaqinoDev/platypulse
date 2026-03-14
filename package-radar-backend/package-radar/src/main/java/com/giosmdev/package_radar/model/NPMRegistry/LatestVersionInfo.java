package com.giosmdev.package_radar.model.NPMRegistry;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LatestVersionInfo {
    private JsonNode repository;
    private JsonNode keywords;
    private JsonNode author;
    private String license;
    private String homepage;
    private List<DependenciesInfo> dependencies;
}
