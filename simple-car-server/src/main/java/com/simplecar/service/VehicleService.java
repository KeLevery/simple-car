package com.simplecar.service;

import com.simplecar.model.dto.AddVehicleRequest;
import com.simplecar.model.entity.Vehicle;

import java.util.List;
import java.util.Map;

public interface VehicleService {
    Vehicle getById(Long carId);

    Map<String, Object> getCarState(Long carId);

    boolean doCarStart(Long carId);

    List<Map<String, Object>> queryByUserId(Long userId);

    Map<String, Object> addVehicle(AddVehicleRequest request);
}
