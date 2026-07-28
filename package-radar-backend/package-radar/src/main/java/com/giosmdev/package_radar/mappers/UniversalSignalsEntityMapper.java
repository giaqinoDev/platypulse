package com.giosmdev.package_radar.mappers;

import org.mapstruct.Mapper;

import com.giosmdev.package_radar.model.DB_Entities.UniversalSignals_Entity;
import com.giosmdev.package_radar.model.universal.UniversalPackageFeatures;

@Mapper(componentModel = "spring") //componentModel tells the mapper to make this interface a Spring bean
public interface UniversalSignalsEntityMapper {
    UniversalSignals_Entity toEntity(UniversalPackageFeatures universal_signals);
}