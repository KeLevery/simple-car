package com.simplecar.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplecar.result.ApiResponse;
import com.simplecar.result.PageResponse;
import com.simplecar.model.dto.AppointmentRequest;
import com.simplecar.model.entity.MaintenanceAppointment;
import com.simplecar.model.entity.MaintenancePlan;
import com.simplecar.model.entity.ServiceStation;
import com.simplecar.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "维保管理")
@RestController
@RequiredArgsConstructor
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    @Operation(summary = "查询维保计划列表")
    @GetMapping("/bs-vehicle-owner/maintenance-plan/randomList")
    public ApiResponse<List<MaintenancePlan>> getPlans() {
        return ApiResponse.success(maintenanceService.getPlans());
    }

    @Operation(summary = "新增维保预约")
    @PostMapping("/bs-vehicle-owner/maintenance-appointment")
    public ApiResponse<Map<String, Object>> createAppointment(@RequestBody AppointmentRequest request) {
        return ApiResponse.success(maintenanceService.createAppointment(request));
    }

    @Operation(summary = "维保记录分页查询")
    @GetMapping("/bs-vehicle-owner/maintenance-appointment/page")
    public PageResponse<MaintenanceAppointment> getAppointmentPage(
            @RequestParam Long carId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<MaintenanceAppointment> page = maintenanceService.getAppointmentPage(carId, pageNum, pageSize);
        return PageResponse.success(page.getRecords(), page.getTotal());
    }

    @Operation(summary = "服务站分页查询")
    @GetMapping("/bs-vehicle-owner/maintenance-service-station/page")
    public PageResponse<ServiceStation> getStationPage(
            @RequestParam(required = false) String cityId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ServiceStation> page = maintenanceService.getStationPage(cityId, pageNum, pageSize);
        return PageResponse.success(page.getRecords(), page.getTotal());
    }

    @Operation(summary = "更新支付状态")
    @PutMapping("/bs-vehicle-owner/maintenance-pay")
    public ApiResponse<Boolean> updatePayStatus(@RequestBody Map<String, Object> params) {
        Long payId = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        Long appointmentId = Long.valueOf(params.get("maintenanceAppointmentId").toString());
        maintenanceService.updatePayStatus(payId, status, appointmentId);
        return ApiResponse.success(true);
    }
}
