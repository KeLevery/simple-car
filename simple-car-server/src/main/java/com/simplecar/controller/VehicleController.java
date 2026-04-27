package com.simplecar.controller;

import com.simplecar.result.ApiResponse;
import com.simplecar.model.dto.AddVehicleRequest;
import com.simplecar.model.entity.Vehicle;
import com.simplecar.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "车辆管理")
@RestController
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    @Operation(summary = "查询车辆摘要信息")
    @GetMapping("/bs-vehicle-owner/carInfo/getByCarId/{carId}")
    public ApiResponse<Vehicle> getCarInfo(@PathVariable Long carId) {
        Vehicle car = vehicleService.getById(carId);
        if (car == null) {
            return ApiResponse.error("车辆不存在");
        }
        return ApiResponse.success(car);
    }

    @Operation(summary = "查询车辆状态")
    @GetMapping("/bs-smart-charger-biz/CarState/{carId}")
    public ApiResponse<Map<String, Object>> getCarState(@PathVariable Long carId) {
        Map<String, Object> data = vehicleService.getCarState(carId);
        if (data == null) {
            return ApiResponse.error("车辆不存在");
        }
        return ApiResponse.success(data);
    }

    @Operation(summary = "远程启动车辆")
    @PostMapping("/bs-vehicle-owner/rc/doCarStart")
    public ApiResponse<Boolean> doCarStart(@RequestBody Map<String, Object> params) {
        Long carId = Long.valueOf(params.get("carId").toString());
        try {
            vehicleService.doCarStart(carId);
            return ApiResponse.success(true);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @Operation(summary = "查询名下车辆")
    @GetMapping("/bs-vehicle-owner/userCar/queryByUserId/{userId}")
    public ApiResponse<List<Map<String, Object>>> queryByUserId(@PathVariable Long userId) {
        return ApiResponse.success(vehicleService.queryByUserId(userId));
    }

    @Operation(summary = "添加车辆")
    @PostMapping("/bs-vehicle-owner/userCar/add")
    public ApiResponse<Map<String, Object>> addCar(@RequestBody AddVehicleRequest request) {
        return ApiResponse.success(vehicleService.addVehicle(request));
    }
}
