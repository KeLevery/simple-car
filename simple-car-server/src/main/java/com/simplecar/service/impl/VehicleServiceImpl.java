package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.dto.AddVehicleRequest;
import com.simplecar.model.entity.Vehicle;
import com.simplecar.model.entity.UserVehicle;
import com.simplecar.mapper.VehicleMapper;
import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleMapper vehicleMapper;
    private final UserVehicleMapper userVehicleMapper;

    public Vehicle getById(Long carId) {
        return vehicleMapper.selectById(carId);
    }

    public Map<String, Object> getCarState(Long carId) {
        Vehicle car = vehicleMapper.selectById(carId);
        if (car == null) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("carId", car.getId());
        data.put("carState", car.getCarState());
        return data;
    }

    public boolean doCarStart(Long carId) {
        Vehicle car = vehicleMapper.selectById(carId);
        if (car == null) {
            return false;
        }
        if (car.getCarState() == 1) {
            throw new RuntimeException("车辆已离线，无法启动");
        }
        car.setCarState(3);
        car.setUpdatedAt(LocalDateTime.now());
        vehicleMapper.updateById(car);
        return true;
    }

    public List<Map<String, Object>> queryByUserId(Long userId) {
        List<UserVehicle> userVehicles = userVehicleMapper.selectList(
                new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getUserId, userId));
        List<Map<String, Object>> result = new ArrayList<>();

        for (UserVehicle uv : userVehicles) {
            Vehicle car = vehicleMapper.selectById(uv.getCarId());
            if (car != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", uv.getId());
                map.put("carID", car.getId());
                map.put("userId", userId);

                Map<String, Object> carData = new HashMap<>();
                carData.put("carId", car.getId());
                carData.put("carName", car.getCarName());
                carData.put("carModels", car.getCarModels());
                carData.put("licenseTag", car.getLicenseTag());
                carData.put("frameNumber", car.getFrameNumber());
                map.put("car", carData);

                result.add(map);
            }
        }
        return result;
    }

    @Transactional
    public Map<String, Object> addVehicle(AddVehicleRequest request) {
        Vehicle car = new Vehicle();
        car.setCarName(request.getCarName());
        car.setCarModels(request.getCarModels());
        car.setLicenseTag(request.getLicenseTag());
        car.setFrameNumber(request.getFrameNumber());
        car.setEnduranceMileage(new BigDecimal("500.00"));
        car.setRemainingPower(100);
        car.setTemperature(new BigDecimal("26.0"));
        car.setCarState(2);
        car.setUpdatedAt(LocalDateTime.now());
        vehicleMapper.insert(car);

        UserVehicle userVehicle = new UserVehicle();
        userVehicle.setUserId(request.getUserId());
        userVehicle.setCarId(car.getId());
        userVehicle.setIsDefault(0);
        userVehicle.setCreatedAt(LocalDateTime.now());
        userVehicleMapper.insert(userVehicle);

        Map<String, Object> result = new HashMap<>();
        result.put("carId", car.getId());
        result.put("carName", car.getCarName());
        result.put("carModels", car.getCarModels());
        result.put("licenseTag", car.getLicenseTag());
        result.put("frameNumber", car.getFrameNumber());
        return result;
    }
}
