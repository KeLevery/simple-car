package com.simplecar.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.mapper.ChargingOrderMapper;
import com.simplecar.mapper.ChargingStationMapper;
import com.simplecar.mapper.CommunityPostMapper;
import com.simplecar.mapper.MaintenanceAppointmentMapper;
import com.simplecar.mapper.RescueRequestMapper;
import com.simplecar.mapper.ServiceStationMapper;
import com.simplecar.mapper.UserMapper;
import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.mapper.VehicleMapper;
import com.simplecar.model.entity.ChargingOrder;
import com.simplecar.model.entity.ChargingStation;
import com.simplecar.model.entity.CommunityPost;
import com.simplecar.model.entity.MaintenanceAppointment;
import com.simplecar.model.entity.RescueRequest;
import com.simplecar.model.entity.ServiceStation;
import com.simplecar.model.entity.User;
import com.simplecar.model.entity.UserVehicle;
import com.simplecar.model.entity.Vehicle;
import com.simplecar.result.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name = "后台管理")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserMapper userMapper;
    private final VehicleMapper vehicleMapper;
    private final UserVehicleMapper userVehicleMapper;
    private final MaintenanceAppointmentMapper appointmentMapper;
    private final RescueRequestMapper rescueRequestMapper;
    private final ChargingStationMapper chargingStationMapper;
    private final ServiceStationMapper serviceStationMapper;
    private final ChargingOrderMapper chargingOrderMapper;
    private final CommunityPostMapper communityPostMapper;
    private final PasswordEncoder passwordEncoder;

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
        List<ChargingOrder> chargingOrders = chargingOrderMapper.selectList(null);
        List<MaintenanceAppointment> appointments = appointmentMapper.selectList(null);

        BigDecimal chargingRevenue = chargingOrders.stream()
                .map(ChargingOrder::getActualPaymentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal maintenanceRevenue = appointments.stream()
                .map(MaintenanceAppointment::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userCount", userMapper.selectCount(null));
        data.put("vehicleCount", vehicleMapper.selectCount(null));
        data.put("appointmentCount", appointmentMapper.selectCount(null));
        data.put("rescueCount", rescueRequestMapper.selectCount(null));
        data.put("chargingStationCount", chargingStationMapper.selectCount(null));
        data.put("serviceStationCount", serviceStationMapper.selectCount(null));
        data.put("postCount", communityPostMapper.selectCount(null));
        data.put("chargingRevenue", chargingRevenue);
        data.put("maintenanceRevenue", maintenanceRevenue);
        data.put("todayAppointmentCount", appointments.stream()
                .filter(item -> LocalDate.now().equals(item.getAppointDate()))
                .count());
        data.put("pendingRescueCount", rescueRequestMapper.selectCount(
                new LambdaQueryWrapper<RescueRequest>().eq(RescueRequest::getStatus, 0)
        ));
        return ApiResponse.success(data);
    }

    @Operation(summary = "用户列表")
    @GetMapping("/users")
    public ApiResponse<List<Map<String, Object>>> users(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer limit
    ) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().orderByDesc(User::getCreatedAt);
        String text = keyword == null ? null : keyword.trim();
        if (text != null && !text.isEmpty()) {
            wrapper.and(query -> {
                query.like(User::getUsername, text)
                        .or().like(User::getNickName, text)
                        .or().like(User::getPhone, text);
                if (text.matches("\\d+")) {
                    query.or().eq(User::getId, Long.valueOf(text));
                }
            });
        }
        if (limit != null && limit > 0) {
            wrapper.last("limit " + Math.min(limit, 50));
        }
        List<Map<String, Object>> users = userMapper.selectList(wrapper).stream().map(this::userView).toList();
        return ApiResponse.success(users);
    }

    @Operation(summary = "新增用户")
    @PostMapping("/users")
    public ApiResponse<Map<String, Object>> createUser(@RequestBody Map<String, Object> params) {
        String username = stringParam(params, "username");
        String password = stringParam(params, "password");
        if (username == null || password == null) {
            return ApiResponse.error("账号和密码不能为空");
        }
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (exists > 0) {
            return ApiResponse.error("账号已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickName(stringParam(params, "nickName"));
        user.setPhone(stringParam(params, "phone"));
        user.setStatus(intParam(params, "status", 1));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return ApiResponse.success(userView(user));
    }

    @Operation(summary = "编辑用户")
    @PutMapping("/users/{id}")
    public ApiResponse<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        user.setNickName(stringParam(params, "nickName"));
        user.setPhone(stringParam(params, "phone"));
        user.setStatus(intParam(params, "status", user.getStatus()));
        String password = stringParam(params, "password");
        if (password != null) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return ApiResponse.success(userView(user));
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/users/{id}/status")
    public ApiResponse<Boolean> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        if (status == null) {
            return ApiResponse.error("状态不能为空");
        }
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        return ApiResponse.success(userMapper.updateById(user) > 0);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/users/{id}")
    @Transactional
    public ApiResponse<Boolean> deleteUser(@PathVariable Long id) {
        List<UserVehicle> relations = userVehicleMapper.selectList(
                new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getUserId, id)
        );
        for (UserVehicle relation : relations) {
            vehicleMapper.deleteById(relation.getCarId());
        }
        userVehicleMapper.delete(new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getUserId, id));
        return ApiResponse.success(userMapper.deleteById(id) > 0);
    }

    @Operation(summary = "车辆列表")
    @GetMapping("/vehicles")
    public ApiResponse<List<Map<String, Object>>> vehicles(@RequestParam(required = false) Long userId) {
        LambdaQueryWrapper<UserVehicle> wrapper = new LambdaQueryWrapper<UserVehicle>()
                .orderByDesc(UserVehicle::getCreatedAt);
        if (userId != null) {
            wrapper.eq(UserVehicle::getUserId, userId);
        }
        List<Map<String, Object>> vehicles = userVehicleMapper.selectList(wrapper).stream()
                .map(this::vehicleView)
                .filter(Objects::nonNull)
                .toList();
        return ApiResponse.success(vehicles);
    }

    @Operation(summary = "新增车辆")
    @PostMapping("/vehicles")
    @Transactional
    public ApiResponse<Map<String, Object>> createVehicle(@RequestBody Map<String, Object> params) {
        Long userId = longParam(params, "userId");
        if (userId == null || userMapper.selectById(userId) == null) {
            return ApiResponse.error("请选择有效用户");
        }
        Vehicle vehicle = new Vehicle();
        fillVehicle(vehicle, params);
        vehicle.setUpdatedAt(LocalDateTime.now());
        vehicleMapper.insert(vehicle);

        UserVehicle relation = new UserVehicle();
        relation.setUserId(userId);
        relation.setCarId(vehicle.getId());
        relation.setIsDefault(intParam(params, "isDefault", 0));
        relation.setCreatedAt(LocalDateTime.now());
        userVehicleMapper.insert(relation);
        return ApiResponse.success(vehicleView(relation));
    }

    @Operation(summary = "编辑车辆")
    @PutMapping("/vehicles/{id}")
    @Transactional
    public ApiResponse<Map<String, Object>> updateVehicle(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            return ApiResponse.error("车辆不存在");
        }
        fillVehicle(vehicle, params);
        vehicle.setUpdatedAt(LocalDateTime.now());
        vehicleMapper.updateById(vehicle);

        UserVehicle relation = userVehicleMapper.selectOne(
                new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getCarId, id).last("limit 1")
        );
        Long userId = longParam(params, "userId");
        if (userId != null) {
            if (userMapper.selectById(userId) == null) {
                return ApiResponse.error("请选择有效用户");
            }
            if (relation == null) {
                relation = new UserVehicle();
                relation.setCarId(id);
                relation.setCreatedAt(LocalDateTime.now());
                relation.setIsDefault(0);
            }
            relation.setUserId(userId);
            if (relation.getId() == null) {
                userVehicleMapper.insert(relation);
            } else {
                userVehicleMapper.updateById(relation);
            }
        }
        return ApiResponse.success(relation == null ? vehicleOnlyView(vehicle) : vehicleView(relation));
    }

    @Operation(summary = "删除车辆")
    @DeleteMapping("/vehicles/{id}")
    @Transactional
    public ApiResponse<Boolean> deleteVehicle(@PathVariable Long id) {
        userVehicleMapper.delete(new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getCarId, id));
        return ApiResponse.success(vehicleMapper.deleteById(id) > 0);
    }

    @Operation(summary = "维保预约列表")
    @GetMapping("/appointments")
    public ApiResponse<List<MaintenanceAppointment>> appointments() {
        return ApiResponse.success(appointmentMapper.selectList(
                new LambdaQueryWrapper<MaintenanceAppointment>().orderByDesc(MaintenanceAppointment::getCreatedAt)
        ));
    }

    @Operation(summary = "更新维保预约状态")
    @PutMapping("/appointments/{id}/status")
    public ApiResponse<Boolean> updateAppointmentStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        if (status == null) {
            return ApiResponse.error("状态不能为空");
        }
        MaintenanceAppointment appointment = new MaintenanceAppointment();
        appointment.setId(id);
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());
        return ApiResponse.success(appointmentMapper.updateById(appointment) > 0);
    }

    @Operation(summary = "救援请求列表")
    @GetMapping("/rescues")
    public ApiResponse<List<RescueRequest>> rescues() {
        return ApiResponse.success(rescueRequestMapper.selectList(
                new LambdaQueryWrapper<RescueRequest>().orderByDesc(RescueRequest::getCreateTime)
        ));
    }

    @Operation(summary = "更新救援状态")
    @PutMapping("/rescues/{id}/status")
    public ApiResponse<Boolean> updateRescueStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        if (status == null) {
            return ApiResponse.error("状态不能为空");
        }
        RescueRequest rescue = new RescueRequest();
        rescue.setId(id);
        rescue.setStatus(status);
        rescue.setUpdateTime(LocalDateTime.now());
        return ApiResponse.success(rescueRequestMapper.updateById(rescue) > 0);
    }

    @Operation(summary = "充电站列表")
    @GetMapping("/charging-stations")
    public ApiResponse<List<ChargingStation>> chargingStations() {
        return ApiResponse.success(chargingStationMapper.selectList(
                new LambdaQueryWrapper<ChargingStation>().orderByDesc(ChargingStation::getCreateTime)
        ));
    }

    @Operation(summary = "新增充电站")
    @PostMapping("/charging-stations")
    public ApiResponse<ChargingStation> createChargingStation(@RequestBody Map<String, Object> params) {
        ChargingStation station = new ChargingStation();
        fillChargingStation(station, params);
        station.setCreateTime(LocalDateTime.now());
        chargingStationMapper.insert(station);
        return ApiResponse.success(station);
    }

    @Operation(summary = "编辑充电站")
    @PutMapping("/charging-stations/{id}")
    public ApiResponse<ChargingStation> updateChargingStation(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        ChargingStation station = chargingStationMapper.selectById(id);
        if (station == null) {
            return ApiResponse.error("充电站不存在");
        }
        fillChargingStation(station, params);
        chargingStationMapper.updateById(station);
        return ApiResponse.success(station);
    }

    @Operation(summary = "更新充电站状态")
    @PutMapping("/charging-stations/{id}/status")
    public ApiResponse<Boolean> updateChargingStationStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        if (status == null) {
            return ApiResponse.error("状态不能为空");
        }
        ChargingStation station = new ChargingStation();
        station.setId(id);
        station.setStatus(status);
        return ApiResponse.success(chargingStationMapper.updateById(station) > 0);
    }

    @Operation(summary = "删除充电站")
    @DeleteMapping("/charging-stations/{id}")
    public ApiResponse<Boolean> deleteChargingStation(@PathVariable Long id) {
        return ApiResponse.success(chargingStationMapper.deleteById(id) > 0);
    }

    @Operation(summary = "服务站列表")
    @GetMapping("/service-stations")
    public ApiResponse<List<ServiceStation>> serviceStations() {
        return ApiResponse.success(serviceStationMapper.selectList(
                new LambdaQueryWrapper<ServiceStation>().orderByDesc(ServiceStation::getId)
        ));
    }

    @Operation(summary = "新增服务站")
    @PostMapping("/service-stations")
    public ApiResponse<ServiceStation> createServiceStation(@RequestBody Map<String, Object> params) {
        ServiceStation station = new ServiceStation();
        fillServiceStation(station, params);
        serviceStationMapper.insert(station);
        return ApiResponse.success(station);
    }

    @Operation(summary = "编辑服务站")
    @PutMapping("/service-stations/{id}")
    public ApiResponse<ServiceStation> updateServiceStation(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        ServiceStation station = serviceStationMapper.selectById(id);
        if (station == null) {
            return ApiResponse.error("服务站不存在");
        }
        fillServiceStation(station, params);
        serviceStationMapper.updateById(station);
        return ApiResponse.success(station);
    }

    @Operation(summary = "删除服务站")
    @DeleteMapping("/service-stations/{id}")
    public ApiResponse<Boolean> deleteServiceStation(@PathVariable Long id) {
        return ApiResponse.success(serviceStationMapper.deleteById(id) > 0);
    }

    @Operation(summary = "社区动态列表")
    @GetMapping("/community-posts")
    public ApiResponse<List<CommunityPost>> communityPosts() {
        return ApiResponse.success(communityPostMapper.selectList(
                new LambdaQueryWrapper<CommunityPost>().orderByDesc(CommunityPost::getCreateTime)
        ));
    }

    @Operation(summary = "删除社区动态")
    @DeleteMapping("/community-posts/{id}")
    public ApiResponse<Boolean> deleteCommunityPost(@PathVariable Long id) {
        return ApiResponse.success(communityPostMapper.deleteById(id) > 0);
    }

    private Map<String, Object> userView(User user) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", user.getId());
        item.put("username", user.getUsername());
        item.put("nickName", user.getNickName());
        item.put("phone", user.getPhone());
        item.put("status", user.getStatus());
        item.put("createdAt", user.getCreatedAt());
        item.put("updatedAt", user.getUpdatedAt());
        return item;
    }

    private Map<String, Object> vehicleView(UserVehicle relation) {
        Vehicle vehicle = vehicleMapper.selectById(relation.getCarId());
        if (vehicle == null) {
            return null;
        }
        Map<String, Object> item = vehicleOnlyView(vehicle);
        User user = userMapper.selectById(relation.getUserId());
        item.put("relationId", relation.getId());
        item.put("userId", relation.getUserId());
        item.put("userName", user == null ? "-" : user.getNickName());
        item.put("username", user == null ? "-" : user.getUsername());
        item.put("isDefault", relation.getIsDefault());
        return item;
    }

    private Map<String, Object> vehicleOnlyView(Vehicle vehicle) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", vehicle.getId());
        item.put("carName", vehicle.getCarName());
        item.put("carModels", vehicle.getCarModels());
        item.put("licenseTag", vehicle.getLicenseTag());
        item.put("frameNumber", vehicle.getFrameNumber());
        item.put("enduranceMileage", vehicle.getEnduranceMileage());
        item.put("remainingPower", vehicle.getRemainingPower());
        item.put("temperature", vehicle.getTemperature());
        item.put("carState", vehicle.getCarState());
        item.put("updatedAt", vehicle.getUpdatedAt());
        return item;
    }

    private void fillVehicle(Vehicle vehicle, Map<String, Object> params) {
        vehicle.setCarName(stringParam(params, "carName"));
        vehicle.setCarModels(stringParam(params, "carModels"));
        vehicle.setLicenseTag(stringParam(params, "licenseTag"));
        vehicle.setFrameNumber(stringParam(params, "frameNumber"));
        vehicle.setEnduranceMileage(decimalParam(params, "enduranceMileage", new BigDecimal("500.00")));
        vehicle.setRemainingPower(intParam(params, "remainingPower", 100));
        vehicle.setTemperature(decimalParam(params, "temperature", new BigDecimal("26.0")));
        vehicle.setCarState(intParam(params, "carState", 1));
    }

    private void fillChargingStation(ChargingStation station, Map<String, Object> params) {
        station.setStationName(stringParam(params, "stationName"));
        station.setAddress(stringParam(params, "address"));
        station.setCityId(stringParam(params, "cityId"));
        station.setTotalPiles(intParam(params, "totalPiles", 0));
        station.setAvailablePiles(intParam(params, "availablePiles", 0));
        station.setStatus(intParam(params, "status", 1));
    }

    private void fillServiceStation(ServiceStation station, Map<String, Object> params) {
        station.setServiceStationName(stringParam(params, "serviceStationName"));
        station.setCityId(stringParam(params, "cityId"));
        station.setAddress(stringParam(params, "address"));
        station.setPhone(stringParam(params, "phone"));
        station.setStatus(intParam(params, "status", 1));
    }

    private String stringParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }

    private Integer intParam(Map<String, Object> params, String key, Integer defaultValue) {
        Object value = params.get(key);
        if (value == null || value.toString().isBlank()) {
            return defaultValue;
        }
        return Integer.valueOf(value.toString());
    }

    private Long longParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return Long.valueOf(value.toString());
    }

    private BigDecimal decimalParam(Map<String, Object> params, String key, BigDecimal defaultValue) {
        Object value = params.get(key);
        if (value == null || value.toString().isBlank()) {
            return defaultValue;
        }
        return new BigDecimal(value.toString());
    }
}
