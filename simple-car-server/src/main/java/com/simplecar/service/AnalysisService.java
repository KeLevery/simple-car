package com.simplecar.service;

import com.simplecar.model.entity.VehicleMileage;

import java.util.List;

public interface AnalysisService {
    List<VehicleMileage> listMileage(Long carId);
}
