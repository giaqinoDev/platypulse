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
    private JsonNode repositry;
    private List<DependenciesInfo> dependencies;
}
