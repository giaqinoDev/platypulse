package com.giosmdev.package_radar.model.NPMRegistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DependenciesInfo {
    private String dependnecy_name;
    private String dependency_version;
    private Boolean depreciated;
}
