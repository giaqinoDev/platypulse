package com.giosmdev.package_radar.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.giosmdev.package_radar.model.SnapShotModel;
import com.giosmdev.package_radar.service.SnapShotsService;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/package/")
public class SnapShotsController {
    private SnapShotsService snap_shot_service;
    public SnapShotsController(SnapShotsService snap_shot_service){
        this.snap_shot_service = snap_shot_service;
    }
    @GetMapping("/{packageId}/snapshots")
    public List<SnapShotModel> getSnapShotsTimeline(@PathVariable UUID packageId) {
        return snap_shot_service.getSnapShots(packageId);
    }
    
}
