package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.mapper.ChargingOrderMapper;
import com.simplecar.mapper.UserMapper;
import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.mapper.VehicleMapper;
import com.simplecar.mapper.VehicleMileageMapper;
import com.simplecar.model.dto.LoginRequest;
import com.simplecar.model.entity.ChargingOrder;
import com.simplecar.model.entity.User;
import com.simplecar.model.entity.UserVehicle;
import com.simplecar.model.entity.Vehicle;
import com.simplecar.model.entity.VehicleMileage;
import com.simplecar.service.AuthService;
import com.simplecar.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final VehicleMapper vehicleMapper;
    private final UserVehicleMapper userVehicleMapper;
    private final ChargingOrderMapper chargingOrderMapper;
    private final VehicleMileageMapper vehicleMileageMapper;
    private final PasswordEncoder passwordEncoder;

    public String login(LoginRequest loginRequest) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, loginRequest.getUsername()));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!matchesPassword(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return jwtUtils.generateToken(loginRequest.getUsername());
    }

    public Map<String, Object> getUserInfo(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return null;
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getId());
        userInfo.put("userName", user.getUsername());
        userInfo.put("nickName", user.getNickName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", "https://img01.yzcdn.cn/vant/cat.jpeg");

        List<UserVehicle> userVehicles = userVehicleMapper.selectList(
                new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getUserId, user.getId()));
        List<Vehicle> vehicles = new ArrayList<>();
        BigDecimal totalCharged = BigDecimal.ZERO;
        BigDecimal totalMileage = BigDecimal.ZERO;

        if (!userVehicles.isEmpty()) {
            List<Long> carIds = userVehicles.stream().map(UserVehicle::getCarId).collect(Collectors.toList());
            vehicles = vehicleMapper.selectBatchIds(carIds);

            for (Long carId : carIds) {
                List<ChargingOrder> orders = chargingOrderMapper.selectList(
                        new LambdaQueryWrapper<ChargingOrder>().eq(ChargingOrder::getCarId, carId));
                for (ChargingOrder order : orders) {
                    if (order.getChargedQuantity() != null) {
                        totalCharged = totalCharged.add(order.getChargedQuantity());
                    }
                }
                VehicleMileage maxMileage = vehicleMileageMapper.selectOne(
                        new LambdaQueryWrapper<VehicleMileage>()
                                .eq(VehicleMileage::getCarId, carId)
                                .orderByDesc(VehicleMileage::getCarMileage)
                                .last("limit 1"));
                if (maxMileage != null && maxMileage.getCarMileage() != null) {
                    totalMileage = totalMileage.add(maxMileage.getCarMileage());
                }
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCharged", totalCharged);
        stats.put("totalMileage", totalMileage);
        stats.put("carCount", vehicles.size());
        userInfo.put("stats", stats);

        List<Map<String, Object>> formattedCars = vehicles.stream().map(car -> {
            Map<String, Object> map = new HashMap<>();
            map.put("carId", car.getId());
            map.put("carName", car.getCarName());
            map.put("carModels", car.getCarModels());
            map.put("licenseTag", car.getLicenseTag());
            map.put("frameNumber", car.getFrameNumber());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("user", userInfo);
        result.put("cars", formattedCars);
        return result;
    }

    public void register(String username, String password, String nickName, String phone) {
        String account = username == null ? "" : username.trim();
        String rawPwd = password == null ? "" : password.trim();
        String nicknameVal = nickName == null ? "" : nickName.trim();
        String phoneVal = phone == null ? "" : phone.trim();

        if (account.isEmpty() || rawPwd.isEmpty()) {
            throw new RuntimeException("账号和密码不能为空");
        }
        if (account.length() != 11) {
            throw new RuntimeException("请输入 11 位手机号");
        }
        if (rawPwd.length() < 6) {
            throw new RuntimeException("密码长度不能少于 6 位");
        }

        User exists = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, account));
        if (exists != null) {
            throw new RuntimeException("该手机号已注册");
        }

        User user = new User();
        user.setUsername(account);
        user.setPassword(passwordEncoder.encode(rawPwd));
        user.setNickName(nicknameVal.isEmpty() ? "用户" + account.substring(account.length() - 4) : nicknameVal);
        user.setPhone(phoneVal.isEmpty() ? account : phoneVal);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
    }

    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        try {
            return passwordEncoder.matches(rawPassword, storedPassword);
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}
