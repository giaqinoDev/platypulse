package com.giosmdev.package_radar.model.DB_Entities;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "raw_signals")
public class RawSignals_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "health_score_id", nullable = false, unique = true)
    private PackageHealthSnapShots_Entity pkgHealthSnapShot;

    @Column(name = "fetched_at", nullable = false)
    private Instant fetched_at;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_data", nullable = false)
    private Map<String, Object> raw_data;
}
