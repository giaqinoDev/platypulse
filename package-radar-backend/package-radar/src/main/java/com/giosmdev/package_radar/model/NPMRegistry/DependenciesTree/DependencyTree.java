package com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependencyTree {
    private String package_name;
    private int tree_depth;

    private List<DependenciesInfo> dependencies;
}
