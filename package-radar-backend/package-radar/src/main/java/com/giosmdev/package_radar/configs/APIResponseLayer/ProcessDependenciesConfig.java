package com.giosmdev.package_radar.configs.APIResponseLayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependenciesInfo;
import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependencyTree;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Configuration
public class ProcessDependenciesConfig {
    private WebClient webClient;
    //Prevent processing of duplicate dependencies
    List<String> omitProcessing = new ArrayList<>();

    private String BASE_PACKAGE_URL ="https://registry.npmjs.org";
    public ProcessDependenciesConfig(WebClient webClient){
        this.webClient = webClient;
    }
    
    @Bean
    public BiFunction<String, JsonNode, Mono<DependencyTree>> genDependenciesTree(){
        return ((String pkg_name, JsonNode topLayerDependencies) -> {
            System.out.println("Top Layer Dependency Tree fetching");
            //Generate initial top layer dependencies container
            List<DependenciesInfo> processedDependencies = genDependencyContainer(topLayerDependencies);

            //Mono<DependencyTree> dependency_tree =  Mono.just(new DependencyTree(pkg_name, 0, getSubDependenciesLayer(processedDependencies)));
            Mono<DependencyTree> dependency_tree = getSubDependenciesLayer(processedDependencies).map(tree -> {
                return new DependencyTree(pkg_name, 0, tree);
            });
            return dependency_tree;
        });
    }
    
    
    public Mono<List<DependenciesInfo>> getSubDependenciesLayer(List<DependenciesInfo> topDependencies){
        if (topDependencies == null || topDependencies.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }

        return Flux.fromIterable(topDependencies)
            .flatMap(dep ->
                fetchSubDependenciesfromReg(dep)
                    .flatMap(subDep ->
                        getSubDependenciesLayer(subDep)
                            .map(processedDeps -> {
                                dep.setDependencies(processedDeps);
                                return dep;
                            })
                    )
            )
            .collectList();
    }
        
    private Mono<List<DependenciesInfo>> fetchSubDependenciesfromReg(DependenciesInfo dep){

        String dependency_name = dep.getDependnecy_name();
        String dependency_version = dep.getDependency_version();

        String version = dependency_version.startsWith("^") ? dependency_version.substring(1) : dependency_version;

        Mono<List<DependenciesInfo>> dependencies_list = webClient.get()
                        .uri(BASE_PACKAGE_URL + "/{dependency_name}", dependency_name)
                        .retrieve()
                        .onStatus(
                            status -> status.is4xxClientError(),
                            response -> Mono.error(new RuntimeException("Package not found"))
                        )
                        .onStatus(
                            status -> status.is5xxServerError(),
                            response -> Mono.error(new RuntimeException("NPM Registry Error"))
                        )
                        .bodyToMono(JsonNode.class)
                        .map(response -> {
                            
                            Boolean deprecated;

                            //Process deprecated Status
                            deprecated = response.path("versions").path(version).has("deprecated");//deprecated = (response.get("versions").get(dependency_version).has("deprecated")) ? true:false;
                            dep.setDepreciated(deprecated);

                            //Generate sub dependencies list
                            JsonNode depsNode = response.path("versions").path(version).path("dependencies");
                            if(depsNode.isMissingNode()){
                                return Collections.emptyList();
                            }
                            List<DependenciesInfo> sub_dependencies = genDependencyContainer(depsNode);
                            omitProcessing.add(dependency_name);

                            if(sub_dependencies == null){
                                return Collections.emptyList();
                            }
                            return sub_dependencies;
                        });
        return dependencies_list;
    }

    private List<DependenciesInfo> genDependencyContainer(JsonNode dependencies){
        if(dependencies == null){
            return null;
        }
        List<DependenciesInfo> dependencyList = new ArrayList<>();

        for(String name : dependencies.propertyNames()){
            String dependencyName = name;
            String dependency_v = dependencies.get(name).asString();

            DependenciesInfo dependency_info = new DependenciesInfo(dependencyName, dependency_v, false, null);
            dependencyList.add(dependency_info);
        };

        return dependencyList;
    }
}
