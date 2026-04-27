package com.simplecar.controller;

import com.simplecar.result.ApiResponse;
import com.simplecar.model.entity.VehicleMileage;
import com.simplecar.model.entity.ChargingOrder;
import com.simplecar.service.AnalysisService;
import com.simplecar.service.ChargingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "数据分析")
@RestController
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;
    private final ChargingService chargingService;

    @Operation(summary = "查询充电订单列表")
    @PostMapping("/bs-vehicle-owner/charging-orders/list")
    public ApiResponse<List<ChargingOrder>> getChargingOrders(@RequestBody Map<String, Object> params) {
        Long carId = Long.valueOf(params.get("carId").toString());
        return ApiResponse.success(chargingService.listOrders(carId));
    }

    @Operation(summary = "查询车辆里程记录")
    @PostMapping("/bs-vehicle-owner/vehicle-mileage/list")
    public ApiResponse<List<VehicleMileage>> getVehicleMileage(@RequestBody Map<String, Object> params) {
        Long carId = Long.valueOf(params.get("carId").toString());
        return ApiResponse.success(analysisService.listMileage(carId));
    }
}
