package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    public Map<String, Object> getViolations(Long userId, Long carId, Integer pageNum, Integer pageSize) {
        List<Long> carIds;
        if (carId != null) {
            carIds = List.of(carId);
        } else {
            List<UserVehicle> userVehicles = userVehicleMapper.selectList(
                    new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getUserId, userId));
            if (userVehicles.isEmpty()) {
                return emptyResult();
            }
            carIds = userVehicles.stream().map(UserVehicle::getCarId).collect(Collectors.toList());
        }

        // 统计保持全量口径（SQL 聚合），list 为当前页
        QueryWrapper<VehicleViolation> summaryWrapper = new QueryWrapper<VehicleViolation>()
                .select("COUNT(*) AS total",
                        "COALESCE(SUM(status = 0), 0) AS untreated",
                        "COALESCE(SUM(fine_amount), 0) AS totalFine",
                        "COALESCE(SUM(deduct_points), 0) AS totalPoints")
                .in("car_id", carIds);
        Map<String, Object> summary = violationMapper.selectMaps(summaryWrapper).stream()
                .findFirst().orElse(Map.of());

        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null ? 10 : Math.max(1, Math.min(pageSize, 100));
        Page<VehicleViolation> page = violationMapper.selectPage(
                new Page<>(num, size),
                new LambdaQueryWrapper<VehicleViolation>()
                        .in(VehicleViolation::getCarId, carIds)
                        .orderByDesc(VehicleViolation::getViolationTime)
        );

        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", summary.getOrDefault("total", 0));
        result.put("untreated", summary.getOrDefault("untreated", 0));
        result.put("totalFine", summary.getOrDefault("totalFine", BigDecimal.ZERO));
        result.put("totalPoints", summary.getOrDefault("totalPoints", 0));
        return result;
    }

    private Map<String, Object> emptyResult() {
        Map<String, Object> empty = new HashMap<>();
        empty.put("list", new ArrayList<>());
        empty.put("total", 0);
        empty.put("untreated", 0L);
        empty.put("totalFine", BigDecimal.ZERO);
        empty.put("totalPoints", 0);
        return empty;
    }
}
