package com.giosmdev.package_radar.configs.APIResponseLayer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final int maxDepth = 1;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final int maxRequests = 50;
    private String BASE_PACKAGE_URL ="https://registry.npmjs.org";

    private Map<String, Mono<List<DependenciesInfo>>> proccessedDeps_cache = new ConcurrentHashMap<>();
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
            Mono<DependencyTree> dependency_tree = getSubDependenciesLayer(processedDependencies, 0).map(tree -> {
                return new DependencyTree(pkg_name, 0, tree);
            });
            return dependency_tree;
        });
    }
    
    
    public Mono<List<DependenciesInfo>> getSubDependenciesLayer(List<DependenciesInfo> topDependencies, int depth){
        System.out.println(depth);
        if (topDependencies == null || topDependencies.isEmpty() || depth > maxDepth) {
            return Mono.just(new ArrayList<DependenciesInfo>());
        }

        return Flux.fromIterable(topDependencies) //Convert list of dependencies to a Flux stream
            .flatMap(dep ->  {                      //Each dependency in the current containerS
                if(requestCount.incrementAndGet() > maxRequests){
                    return Flux.just(dep);
                }

                return fetchSubDependenciesfromReg(dep)   // Fetch sub dependencies of dependency
                    .flatMapMany(Flux::fromIterable) // convert from Mono<List<DependenciesInfo>> to Flux<DependneciesInfo>
                    .flatMap(subDep ->               // Recursivley call each child of and get their dependency
                        getSubDependenciesLayer(List.of(subDep), depth + 1)
                        .flatMapMany(Flux::fromIterable)
                    )
                    .collectList()                  //Collect subdependnecies into a list
                    .flatMapMany(processedDeps -> {         //Itterate over processed subdependnecies and connect them to parent dependnencies
                        dep.setDependencies(processedDeps);
                        return Flux.just(dep);
                    });
            },1, 1)       //Return Flux<DependenciesInfo>
            .collectList(); //Collect List and convert from Flux<DependenciesInfo> -> Mono<List<DependenciesInfo>>
    }
        
    private Mono<List<DependenciesInfo>> fetchSubDependenciesfromReg(DependenciesInfo dep){

        String dependency_name = dep.getDependnecy_name();
        String dependency_version = dep.getDependency_version();
        String key = dependency_name + "@" + dependency_version;
        String version = dependency_version.startsWith("^") ? dependency_version.substring(1) : dependency_version;

        return proccessedDeps_cache.computeIfAbsent(key, k -> 
            webClient.get()
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
                        .timeout(Duration.ofSeconds(2))
                        .map(response -> {
                            
                            Boolean deprecated;

                            //Process deprecated Status
                            deprecated = response.path("versions").path(version).has("deprecated");//deprecated = (response.get("versions").get(dependency_version).has("deprecated")) ? true:false;
                            dep.setDepreciated(deprecated);

                            //Generate sub dependencies list
                            JsonNode depsNode = response.path("versions").path(version).path("dependencies");
                            if(depsNode.isMissingNode()){
                                return new ArrayList<DependenciesInfo>();
                            }
                            List<DependenciesInfo> sub_dependencies = genDependencyContainer(depsNode);
                            omitProcessing.add(dependency_name);

                            if(sub_dependencies == null){
                                return new ArrayList<DependenciesInfo>();
                            }
                            return sub_dependencies;
                        })
                        .cache()
        );
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
