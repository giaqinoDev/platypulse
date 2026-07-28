package com.giosmdev.package_radar.model.DB_Entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "packages", uniqueConstraints = {@UniqueConstraint(columnNames = {"eco_id", "package_name"})})
public class PackagesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "eco_id", nullable = false)
    private EcosystemEntity ecosystem;

    @Column(name = "package_name", nullable = false, length = 255)
    private String packageName;

    @OneToOne(optional = true)
    @JoinColumn(name = "latest_report_id")
    private PackageHealthSnapShots_Entity latest_report;

}
