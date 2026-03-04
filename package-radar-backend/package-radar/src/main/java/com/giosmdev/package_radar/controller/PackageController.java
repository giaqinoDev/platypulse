package com.giosmdev.package_radar.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Services
import com.giosmdev.package_radar.service.NPMService;

import reactor.core.publisher.Mono;

//Models
import com.giosmdev.package_radar.model.Report;

@RestController //combines @Controller and @ResponseBody: Tells SB this class handles http requests and return JSON
@RequestMapping("/api/packages") //maps http requests to specific controller classes: base url for all endpoints in this controller
public class PackageController {

    //Constructor injection: prefered way over auto injection
    private final NPMService packageService;

    public PackageController(NPMService packageService){
        this.packageService = packageService;
    }

    @GetMapping("/{pkgName}")
    public Mono<Object> getReport(@PathVariable String pkgName){
        return packageService.getReport(pkgName);
    }

    
}
