package com.giosmdev.package_radar.model.DB_Entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter //Generates getter method for every field
@Setter //Generates setter method for every field
@AllArgsConstructor
@NoArgsConstructor //Required by hibernate/jpa to create empty obj when it is reading from the DB
@Entity //Tells hibernate: this entity maps to a DB table
@Table(name = "ecosystem") //Tells hibernate: name table "ecosystem", not EcosystemEntity
public class EcosystemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "eco", nullable = false, unique = true, length = 50)
    private String eco;

    @Column(name = "language", nullable = false, length = 50)
    private String language;

}
