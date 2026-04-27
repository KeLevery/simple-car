package com.simplecar.service;

import java.util.Map;

public interface ViolationService {
    Map<String, Object> getViolations(Long userId, Long carId);
}
