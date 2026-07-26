package com.simplecar.controller;

import com.simplecar.model.entity.ChargingStation;
import com.simplecar.model.entity.CommunityPost;
import com.simplecar.model.entity.MaintenanceAppointment;
import com.simplecar.model.entity.RescueRequest;
import com.simplecar.model.entity.ServiceStation;
import com.simplecar.result.ApiResponse;
import com.simplecar.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "后台管理")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "校验后台登录态")
    @GetMapping("/session")
    public ApiResponse<Map<String, Object>> session(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.error(401, "未登录或登录已过期");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("username", authentication.getName());
        data.put("roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        data.put("authenticated", true);
        return ApiResponse.success(data);
    }

    @Operation(summary = "后台总览")
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        return ApiResponse.success(adminService.overview());
    }

    @Operation(summary = "用户列表")
    @GetMapping("/users")
    public ApiResponse<List<Map<String, Object>>> users(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer limit
    ) {
        return ApiResponse.success(adminService.listUsers(keyword, limit));
    }

    @Operation(summary = "新增用户")
    @PostMapping("/users")
    public ApiResponse<Map<String, Object>> createUser(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(adminService.createUser(params));
    }

    @Operation(summary = "编辑用户")
    @PutMapping("/users/{id}")
    public ApiResponse<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return ApiResponse.success(adminService.updateUser(id, params));
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/users/{id}/status")
    public ApiResponse<Boolean> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        return ApiResponse.success(adminService.updateUserStatus(id, params.get("status")));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/users/{id}")
    public ApiResponse<Boolean> deleteUser(@PathVariable Long id) {
        return ApiResponse.success(adminService.deleteUser(id));
    }

    @Operation(summary = "车辆列表")
    @GetMapping("/vehicles")
    public ApiResponse<List<Map<String, Object>>> vehicles(@RequestParam(required = false) Long userId) {
        return ApiResponse.success(adminService.listVehicles(userId));
    }

    @Operation(summary = "新增车辆")
    @PostMapping("/vehicles")
    public ApiResponse<Map<String, Object>> createVehicle(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(adminService.createVehicle(params));
    }

    @Operation(summary = "编辑车辆")
    @PutMapping("/vehicles/{id}")
    public ApiResponse<Map<String, Object>> updateVehicle(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return ApiResponse.success(adminService.updateVehicle(id, params));
    }

    @Operation(summary = "删除车辆")
    @DeleteMapping("/vehicles/{id}")
    public ApiResponse<Boolean> deleteVehicle(@PathVariable Long id) {
        return ApiResponse.success(adminService.deleteVehicle(id));
    }

    @Operation(summary = "维保预约列表")
    @GetMapping("/appointments")
    public ApiResponse<List<MaintenanceAppointment>> appointments() {
        return ApiResponse.success(adminService.listAppointments());
    }

    @Operation(summary = "更新维保预约状态")
    @PutMapping("/appointments/{id}/status")
    public ApiResponse<Boolean> updateAppointmentStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        return ApiResponse.success(adminService.updateAppointmentStatus(id, params.get("status")));
    }

    @Operation(summary = "救援请求列表")
    @GetMapping("/rescues")
    public ApiResponse<List<RescueRequest>> rescues() {
        return ApiResponse.success(adminService.listRescues());
    }

    @Operation(summary = "更新救援状态")
    @PutMapping("/rescues/{id}/status")
    public ApiResponse<Boolean> updateRescueStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        return ApiResponse.success(adminService.updateRescueStatus(id, params.get("status")));
    }

    @Operation(summary = "充电站列表")
    @GetMapping("/charging-stations")
    public ApiResponse<List<ChargingStation>> chargingStations() {
        return ApiResponse.success(adminService.listChargingStations());
    }

    @Operation(summary = "新增充电站")
    @PostMapping("/charging-stations")
    public ApiResponse<ChargingStation> createChargingStation(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(adminService.createChargingStation(params));
    }

    @Operation(summary = "编辑充电站")
    @PutMapping("/charging-stations/{id}")
    public ApiResponse<ChargingStation> updateChargingStation(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return ApiResponse.success(adminService.updateChargingStation(id, params));
    }

    @Operation(summary = "更新充电站状态")
    @PutMapping("/charging-stations/{id}/status")
    public ApiResponse<Boolean> updateChargingStationStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        return ApiResponse.success(adminService.updateChargingStationStatus(id, params.get("status")));
    }

    @Operation(summary = "删除充电站")
    @DeleteMapping("/charging-stations/{id}")
    public ApiResponse<Boolean> deleteChargingStation(@PathVariable Long id) {
        return ApiResponse.success(adminService.deleteChargingStation(id));
    }

    @Operation(summary = "服务站列表")
    @GetMapping("/service-stations")
    public ApiResponse<List<ServiceStation>> serviceStations() {
        return ApiResponse.success(adminService.listServiceStations());
    }

    @Operation(summary = "新增服务站")
    @PostMapping("/service-stations")
    public ApiResponse<ServiceStation> createServiceStation(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(adminService.createServiceStation(params));
    }

    @Operation(summary = "编辑服务站")
    @PutMapping("/service-stations/{id}")
    public ApiResponse<ServiceStation> updateServiceStation(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return ApiResponse.success(adminService.updateServiceStation(id, params));
    }

    @Operation(summary = "删除服务站")
    @DeleteMapping("/service-stations/{id}")
    public ApiResponse<Boolean> deleteServiceStation(@PathVariable Long id) {
        return ApiResponse.success(adminService.deleteServiceStation(id));
    }

    @Operation(summary = "社区动态列表")
    @GetMapping("/community-posts")
    public ApiResponse<List<CommunityPost>> communityPosts() {
        return ApiResponse.success(adminService.listCommunityPosts());
    }

    @Operation(summary = "删除社区动态")
    @DeleteMapping("/community-posts/{id}")
    public ApiResponse<Boolean> deleteCommunityPost(@PathVariable Long id) {
        return ApiResponse.success(adminService.deleteCommunityPost(id));
    }
}
