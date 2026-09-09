package com.giosmdev.package_radar.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.giosmdev.package_radar.exceptions.DataBaseExceptions.SnapShotsNotFoundException;
import com.giosmdev.package_radar.model.SnapShotModel;
import com.giosmdev.package_radar.model.DB_Entities.PackageHealthSnapShots_Entity;
import com.giosmdev.package_radar.repository.PackageHealthScoreRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SnapShotsService {
    private PackageHealthScoreRepository pkg_health_score_repo;
    private HttpServletRequest requestServlet;

    public SnapShotsService(PackageHealthScoreRepository pkg_health_score_repo, HttpServletRequest requestServlet){
        this.pkg_health_score_repo =pkg_health_score_repo;
        this.requestServlet = requestServlet;
    }
    public List<SnapShotModel> getSnapShots(UUID pkgId){
        Optional<List<PackageHealthSnapShots_Entity>> fetch_health_scores = pkg_health_score_repo.findByPkg_IdOrderByCreatedAtDesc(pkgId);
        if(!fetch_health_scores.isPresent()){
            System.out.println("Failed SnapShots fetching");
            throw new SnapShotsNotFoundException("Package report snap shots not found", requestServlet.getRequestURI().toString());
        }
        List<PackageHealthSnapShots_Entity> raw_health_score_snapShots = fetch_health_scores.get();
        List<SnapShotModel> formatted_heath_scores = new ArrayList<SnapShotModel>();
        for(PackageHealthSnapShots_Entity health_score : raw_health_score_snapShots){
            SnapShotModel formatted_score = new SnapShotModel(health_score.getId(), health_score.getAggregate_score(), health_score.getCreatedAt());
            formatted_heath_scores.add(formatted_score);
        }
        return formatted_heath_scores;

    }
}
