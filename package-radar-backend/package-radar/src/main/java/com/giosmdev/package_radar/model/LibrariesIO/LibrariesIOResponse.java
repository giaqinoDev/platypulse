package com.giosmdev.package_radar.model.LibrariesIO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibrariesIOResponse {
    private int dependent_packages_count; //Counts published packages and measures library to library use
    private int dependent_repos_count; //Counts code repositories and measures actual project adoption
}
