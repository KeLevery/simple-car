package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplecar.mapper.ChargingOrderMapper;
import com.simplecar.mapper.ChargingStationMapper;
import com.simplecar.mapper.CommunityPostMapper;
import com.simplecar.mapper.MaintenanceAppointmentMapper;
import com.simplecar.mapper.RescueRequestMapper;
import com.simplecar.mapper.ServiceStationMapper;
import com.simplecar.mapper.UserMapper;
import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.mapper.VehicleMapper;
import com.simplecar.model.entity.ChargingStation;
import com.simplecar.model.entity.CommunityPost;
import com.simplecar.model.entity.MaintenanceAppointment;
import com.simplecar.model.entity.RescueRequest;
import com.simplecar.model.entity.ServiceStation;
import com.simplecar.model.entity.User;
import com.simplecar.model.entity.UserVehicle;
import com.simplecar.model.entity.Vehicle;
import com.simplecar.result.PagedData;
import com.simplecar.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
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

    @Override
    public Map<String, Object> overview() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userCount", userMapper.selectCount(null));
        data.put("vehicleCount", vehicleMapper.selectCount(null));
        data.put("appointmentCount", appointmentMapper.selectCount(null));
        data.put("rescueCount", rescueRequestMapper.selectCount(null));
        data.put("chargingStationCount", chargingStationMapper.selectCount(null));
        data.put("serviceStationCount", serviceStationMapper.selectCount(null));
        data.put("postCount", communityPostMapper.selectCount(null));
        data.put("chargingRevenue", chargingOrderMapper.selectTotalActualPaymentAmount());
        data.put("maintenanceRevenue", appointmentMapper.selectTotalAmount());
        data.put("todayAppointmentCount", appointmentMapper.selectCountByAppointDate(LocalDate.now()));
        data.put("pendingRescueCount", rescueRequestMapper.selectCount(
                new LambdaQueryWrapper<RescueRequest>().eq(RescueRequest::getStatus, 0)
        ));
        return data;
    }

    @Override
    public PagedData<Map<String, Object>> listUsers(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().orderByDesc(User::getCreatedAt);
        String text = keyword == null ? null : keyword.trim();
        if (text != null && !text.isEmpty()) {
            String finalText = text;
            wrapper.and(query -> {
                query.like(User::getUsername, finalText)
                        .or().like(User::getNickName, finalText)
                        .or().like(User::getPhone, finalText);
                if (finalText.matches("\\d+")) {
                    query.or().eq(User::getId, Long.valueOf(finalText));
                }
            });
        }
        Page<User> page = userMapper.selectPage(newPage(pageNum, pageSize), wrapper);
        return new PagedData<>(page.getRecords().stream().map(this::userView).toList(), page.getTotal());
    }

    @Override
    public Map<String, Object> createUser(Map<String, Object> params) {
        String username = stringParam(params, "username");
        String password = stringParam(params, "password");
        if (username == null || password == null) {
            throw new RuntimeException("账号和密码不能为空");
        }
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (exists > 0) {
            throw new RuntimeException("账号已存在");
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
        return userView(user);
    }

    @Override
    public Map<String, Object> updateUser(Long id, Map<String, Object> params) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
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
        return userView(user);
    }

    @Override
    public boolean updateUserStatus(Long id, Integer status) {
        if (status == null) {
            throw new RuntimeException("状态不能为空");
        }
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        List<UserVehicle> relations = userVehicleMapper.selectList(
                new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getUserId, id)
        );
        if (!relations.isEmpty()) {
            vehicleMapper.deleteBatchIds(relations.stream().map(UserVehicle::getCarId).toList());
        }
        userVehicleMapper.delete(new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getUserId, id));
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public PagedData<Map<String, Object>> listVehicles(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<UserVehicle> wrapper = new LambdaQueryWrapper<UserVehicle>()
                .orderByDesc(UserVehicle::getCreatedAt);
        if (userId != null) {
            wrapper.eq(UserVehicle::getUserId, userId);
        }
        Page<UserVehicle> page = userVehicleMapper.selectPage(newPage(pageNum, pageSize), wrapper);
        List<UserVehicle> relations = page.getRecords();
        if (relations.isEmpty()) {
            return new PagedData<>(List.of(), page.getTotal());
        }

        List<Long> carIds = relations.stream().map(UserVehicle::getCarId).distinct().toList();
        Map<Long, Vehicle> vehicleById = vehicleMapper.selectBatchIds(carIds).stream()
                .collect(Collectors.toMap(Vehicle::getId, vehicle -> vehicle));
        List<Long> userIds = relations.stream().map(UserVehicle::getUserId).distinct().toList();
        Map<Long, User> userById = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<Map<String, Object>> rows = relations.stream()
                .map(relation -> vehicleView(
                        relation,
                        vehicleById.get(relation.getCarId()),
                        userById.get(relation.getUserId())
                ))
                .filter(Objects::nonNull)
                .toList();
        return new PagedData<>(rows, page.getTotal());
    }

    @Override
    @Transactional
    public Map<String, Object> createVehicle(Map<String, Object> params) {
        Long userId = longParam(params, "userId");
        if (userId == null || userMapper.selectById(userId) == null) {
            throw new RuntimeException("请选择有效用户");
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
        return vehicleView(relation);
    }

    @Override
    @Transactional
    public Map<String, Object> updateVehicle(Long id, Map<String, Object> params) {
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new RuntimeException("车辆不存在");
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
                throw new RuntimeException("请选择有效用户");
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
        return relation == null ? vehicleOnlyView(vehicle) : vehicleView(relation);
    }

    @Override
    @Transactional
    public boolean deleteVehicle(Long id) {
        userVehicleMapper.delete(new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getCarId, id));
        return vehicleMapper.deleteById(id) > 0;
    }

    @Override
    public PagedData<MaintenanceAppointment> listAppointments(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<MaintenanceAppointment> wrapper = new LambdaQueryWrapper<MaintenanceAppointment>()
                .orderByDesc(MaintenanceAppointment::getCreatedAt);
        String text = trimmedKeyword(keyword);
        if (text != null) {
            wrapper.and(query -> query.like(MaintenanceAppointment::getWorkNo, text)
                    .or().like(MaintenanceAppointment::getCustomerName, text)
                    .or().like(MaintenanceAppointment::getCustomerPhone, text));
        }
        Page<MaintenanceAppointment> page = appointmentMapper.selectPage(newPage(pageNum, pageSize), wrapper);
        return new PagedData<>(page.getRecords(), page.getTotal());
    }

    @Override
    public boolean updateAppointmentStatus(Long id, Integer status) {
        if (status == null) {
            throw new RuntimeException("状态不能为空");
        }
        MaintenanceAppointment appointment = new MaintenanceAppointment();
        appointment.setId(id);
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentMapper.updateById(appointment) > 0;
    }

    @Override
    public PagedData<RescueRequest> listRescues(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<RescueRequest> wrapper = new LambdaQueryWrapper<RescueRequest>()
                .orderByDesc(RescueRequest::getCreateTime);
        String text = trimmedKeyword(keyword);
        if (text != null) {
            wrapper.and(query -> query.like(RescueRequest::getContactName, text)
                    .or().like(RescueRequest::getContactPhone, text)
                    .or().like(RescueRequest::getLocation, text));
        }
        Page<RescueRequest> page = rescueRequestMapper.selectPage(newPage(pageNum, pageSize), wrapper);
        return new PagedData<>(page.getRecords(), page.getTotal());
    }

    @Override
    public boolean updateRescueStatus(Long id, Integer status) {
        if (status == null) {
            throw new RuntimeException("状态不能为空");
        }
        RescueRequest rescue = new RescueRequest();
        rescue.setId(id);
        rescue.setStatus(status);
        rescue.setUpdateTime(LocalDateTime.now());
        return rescueRequestMapper.updateById(rescue) > 0;
    }

    @Override
    public PagedData<ChargingStation> listChargingStations(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ChargingStation> wrapper = new LambdaQueryWrapper<ChargingStation>()
                .orderByDesc(ChargingStation::getCreateTime);
        String text = trimmedKeyword(keyword);
        if (text != null) {
            wrapper.and(query -> query.like(ChargingStation::getStationName, text)
                    .or().like(ChargingStation::getCityId, text)
                    .or().like(ChargingStation::getAddress, text));
        }
        Page<ChargingStation> page = chargingStationMapper.selectPage(newPage(pageNum, pageSize), wrapper);
        return new PagedData<>(page.getRecords(), page.getTotal());
    }

    @Override
    public ChargingStation createChargingStation(Map<String, Object> params) {
        ChargingStation station = new ChargingStation();
        fillChargingStation(station, params);
        station.setCreateTime(LocalDateTime.now());
        chargingStationMapper.insert(station);
        return station;
    }

    @Override
    public ChargingStation updateChargingStation(Long id, Map<String, Object> params) {
        ChargingStation station = chargingStationMapper.selectById(id);
        if (station == null) {
            throw new RuntimeException("充电站不存在");
        }
        fillChargingStation(station, params);
        chargingStationMapper.updateById(station);
        return station;
    }

    @Override
    public boolean updateChargingStationStatus(Long id, Integer status) {
        if (status == null) {
            throw new RuntimeException("状态不能为空");
        }
        ChargingStation station = new ChargingStation();
        station.setId(id);
        station.setStatus(status);
        return chargingStationMapper.updateById(station) > 0;
    }

    @Override
    public boolean deleteChargingStation(Long id) {
        return chargingStationMapper.deleteById(id) > 0;
    }

    @Override
    public PagedData<ServiceStation> listServiceStations(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ServiceStation> wrapper = new LambdaQueryWrapper<ServiceStation>()
                .orderByDesc(ServiceStation::getId);
        String text = trimmedKeyword(keyword);
        if (text != null) {
            wrapper.and(query -> query.like(ServiceStation::getServiceStationName, text)
                    .or().like(ServiceStation::getCityId, text)
                    .or().like(ServiceStation::getAddress, text));
        }
        Page<ServiceStation> page = serviceStationMapper.selectPage(newPage(pageNum, pageSize), wrapper);
        return new PagedData<>(page.getRecords(), page.getTotal());
    }

    @Override
    public ServiceStation createServiceStation(Map<String, Object> params) {
        ServiceStation station = new ServiceStation();
        fillServiceStation(station, params);
        serviceStationMapper.insert(station);
        return station;
    }

    @Override
    public ServiceStation updateServiceStation(Long id, Map<String, Object> params) {
        ServiceStation station = serviceStationMapper.selectById(id);
        if (station == null) {
            throw new RuntimeException("服务站不存在");
        }
        fillServiceStation(station, params);
        serviceStationMapper.updateById(station);
        return station;
    }

    @Override
    public boolean deleteServiceStation(Long id) {
        return serviceStationMapper.deleteById(id) > 0;
    }

    @Override
    public PagedData<CommunityPost> listCommunityPosts(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<CommunityPost> wrapper = new LambdaQueryWrapper<CommunityPost>()
                .orderByDesc(CommunityPost::getCreateTime);
        String text = trimmedKeyword(keyword);
        if (text != null) {
            wrapper.like(CommunityPost::getContent, text);
        }
        Page<CommunityPost> page = communityPostMapper.selectPage(newPage(pageNum, pageSize), wrapper);
        return new PagedData<>(page.getRecords(), page.getTotal());
    }

    @Override
    public boolean deleteCommunityPost(Long id) {
        return communityPostMapper.deleteById(id) > 0;
    }

    private <T> Page<T> newPage(Integer pageNum, Integer pageSize) {
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null ? 10 : Math.max(1, Math.min(pageSize, 100));
        return new Page<>(num, size);
    }

    private String trimmedKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String text = keyword.trim();
        return text.isEmpty() ? null : text;
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
        User user = userMapper.selectById(relation.getUserId());
        return vehicleView(relation, vehicle, user);
    }

    private Map<String, Object> vehicleView(UserVehicle relation, Vehicle vehicle, User user) {
        if (vehicle == null) {
            return null;
        }
        Map<String, Object> item = vehicleOnlyView(vehicle);
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
