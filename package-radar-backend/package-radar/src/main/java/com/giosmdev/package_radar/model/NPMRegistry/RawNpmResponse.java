package com.giosmdev.package_radar.model.NPMRegistry;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawNpmResponse {

    private String pkge_name;
    private String latest_version;
    private JsonNode latest_version_info;
}
