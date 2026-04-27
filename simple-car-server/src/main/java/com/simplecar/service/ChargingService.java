package com.simplecar.service;

import com.simplecar.model.entity.ChargingOrder;
import com.simplecar.model.entity.ChargingStation;

import java.util.List;

public interface ChargingService {
    List<ChargingStation> listByCity(String cityId, Boolean availableOnly);

    List<ChargingOrder> listOrders(Long carId);
}
