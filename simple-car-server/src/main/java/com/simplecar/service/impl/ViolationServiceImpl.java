package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.entity.UserVehicle;
import com.simplecar.model.entity.VehicleViolation;
import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.mapper.VehicleViolationMapper;
import com.simplecar.service.ViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViolationServiceImpl implements ViolationService {
    private final VehicleViolationMapper violationMapper;
    private final UserVehicleMapper userVehicleMapper;

    public Map<String, Object> getViolations(Long userId, Long carId) {
        List<VehicleViolation> violations;
        if (carId != null) {
            violations = violationMapper.selectList(
                    new LambdaQueryWrapper<VehicleViolation>()
                            .eq(VehicleViolation::getCarId, carId)
                            .orderByDesc(VehicleViolation::getViolationTime)
            );
        } else {
            List<UserVehicle> userVehicles = userVehicleMapper.selectList(
                    new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getUserId, userId));
            if (userVehicles.isEmpty()) {
                Map<String, Object> empty = new HashMap<>();
                empty.put("list", new ArrayList<>());
                empty.put("total", 0);
                empty.put("untreated", 0L);
                empty.put("totalFine", BigDecimal.ZERO);
                empty.put("totalPoints", 0);
                return empty;
            }
            List<Long> carIds = userVehicles.stream().map(UserVehicle::getCarId).collect(Collectors.toList());
            violations = violationMapper.selectList(
                    new LambdaQueryWrapper<VehicleViolation>()
                            .in(VehicleViolation::getCarId, carIds)
                            .orderByDesc(VehicleViolation::getViolationTime)
            );
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", violations);
        result.put("total", violations.size());
        result.put("untreated", violations.stream().filter(v -> v.getStatus() == 0).count());
        result.put("totalFine", violations.stream()
                .map(v -> v.getFineAmount() != null ? v.getFineAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        result.put("totalPoints", violations.stream()
                .mapToInt(v -> v.getDeductPoints() != null ? v.getDeductPoints() : 0).sum());
        return result;
    }
}
