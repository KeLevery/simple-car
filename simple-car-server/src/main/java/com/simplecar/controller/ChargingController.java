package com.simplecar.controller;

import com.simplecar.result.ApiResponse;
import com.simplecar.model.entity.ChargingStation;
import com.simplecar.service.ChargingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "充电管理")
@RestController
@RequiredArgsConstructor
public class ChargingController {
    private final ChargingService chargingService;

    @Operation(summary = "获取充电站列表")
    @GetMapping("/charging-station/list")
    public ApiResponse<List<ChargingStation>> listStations(
            @RequestParam(required = false) String cityId,
            @RequestParam(defaultValue = "false") Boolean availableOnly) {
        return ApiResponse.success(chargingService.listByCity(cityId, availableOnly));
    }
}
