package com.giosmdev.package_radar.model.DB_Entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "package_health_snapshots")
public class PackageHealthSnapShots_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private PackagesEntity pkg;
    
    //Check constraint is handled by sql schema
    @Column(name = "aggregate_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal aggregate_score; //Instead of storing as double (binary approximate floating point numbers) BigDecimal stores as exact decimal

    @Column(name = "activity_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal activity_score;

    @Column(name = "maintenance_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal maintenance_score;

    @Column(name = "adoption_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal adoption_score;

    @Column(name = "risk_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal risk_score;

    //No need to handle default now()
    @Column(name = "created_at", nullable = false)
    private Instant created_at;
    
    @Column(name = "algorithm_v", nullable = false)
    private String algorithm_v;
}
