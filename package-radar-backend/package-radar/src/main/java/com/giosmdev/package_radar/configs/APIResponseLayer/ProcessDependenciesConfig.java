package com.giosmdev.package_radar.configs.APIResponseLayer;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependenciesInfo;
import com.giosmdev.package_radar.model.NPMRegistry.DependenciesTree.DependencyTree;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tools.jackson.databind.JsonNode;

@Configuration
public class ProcessDependenciesConfig {
    private WebClient webClient;

    private final int maxDepth = 5;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final int maxRequests = 100;

    private String BASE_PACKAGE_URL ="https://registry.npmjs.org";

    Map<String, Mono<JsonNode>> proccessedDeps_cache = new ConcurrentHashMap<>();
    public ProcessDependenciesConfig(WebClient webClient){
        this.webClient = webClient;
    }

    private static class DepNode {
        DependenciesInfo dep;
        int depth;

        DepNode(DependenciesInfo dep, int depth) {
            this.dep = dep;
            this.depth = depth;
        }
    }
    @Bean
    public BiFunction<String, JsonNode, Mono<DependencyTree>> genDependenciesTree() {
        return (pkgName, topLayerDependencies) ->
            Mono.fromCallable(() -> {  //fromCallable allows you to wrap a value that is possibly a reuslt of a blocking operation
                                       //into a Mono. This allows you to incorporate it into existsing reactive pipelines
                //Prepare chache and request counter through reset
                requestCount.set(0);
                proccessedDeps_cache.clear();

                //Gen top layer dependnecies (aka direct dependnecies of queried package)
                List<DependenciesInfo> roots = genDependencyContainer(topLayerDependencies);

                //BFS Operations
                Queue<DepNode> queue = new ArrayDeque<>();
                roots.forEach(dep -> queue.add(new DepNode(dep, 0)));

                while (!queue.isEmpty()) {
                    DepNode node = queue.poll();

                    if (node.depth >= maxDepth){
                        continue;
                    }
                    if (requestCount.get() >= maxRequests){
                        break;
                    }

                    requestCount.incrementAndGet();

                    List<DependenciesInfo> children = fetchSubDependencies(node.dep); //blocking operation!

                    if (children.isEmpty()){
                        continue;
                    }

                    node.dep.setDependencies(children);

                    for (DependenciesInfo child : children) {
                        queue.add(new DepNode(child, node.depth + 1));
                    }
                }

                return new DependencyTree(pkgName, 0, roots);

            }).subscribeOn(Schedulers.boundedElastic());//Run blockign code on a thread from the boundedElastic pool
    }
    
    private List<DependenciesInfo> fetchSubDependencies(DependenciesInfo dep) {
        String name = dep.getDependnecy_name();
        String versionRaw = dep.getDependency_version();
        String version = versionRaw.startsWith("^") ? versionRaw.substring(1) : versionRaw;

        String key = name + "@" + version;

        try {
            JsonNode response = proccessedDeps_cache
                .computeIfAbsent(key, k ->
                    webClient.get()
                        .uri(BASE_PACKAGE_URL + "/{name}", name)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .timeout(Duration.ofSeconds(2))
                        .onErrorResume(e -> Mono.empty())
                        .cache() //ensures subsequent subscribers share the same Mono instead of calling API again
                )
                .block(); //essentially completes the Mono, extracting the JsonNode out of Mono<JsonNode> and returning it
                

            if (response == null){
                return Collections.emptyList();
            }

            boolean deprecated = response.path("versions")
                                        .path(version)
                                        .has("deprecated");

            dep.setDepreciated(deprecated);

            JsonNode depsNode = response.path("versions")
                                        .path(version)
                                        .path("dependencies");

            if (depsNode.isMissingNode()) {
                return Collections.emptyList();
            }

            return genDependencyContainer(depsNode);

        } catch (Exception e) {
            return Collections.emptyList();
        }
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
