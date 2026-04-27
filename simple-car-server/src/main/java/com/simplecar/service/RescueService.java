package com.simplecar.service;

import com.simplecar.model.entity.RescueRequest;

import java.util.List;

public interface RescueService {
    RescueRequest create(RescueRequest request, Long userId);

    List<RescueRequest> listByUser(Long userId);
}
