package com.simplecar.service.impl;

import com.simplecar.mapper.ChargingOrderMapper;
import com.simplecar.mapper.UserMapper;
import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.mapper.VehicleMapper;
import com.simplecar.mapper.VehicleMileageMapper;
import com.simplecar.model.dto.LoginRequest;
import com.simplecar.model.entity.User;
import com.simplecar.model.entity.UserVehicle;
import com.simplecar.model.entity.Vehicle;
import com.simplecar.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loginAcceptsEncodedPassword() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserMapper userMapper = mock(UserMapper.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        AuthServiceImpl service = createService(jwtUtils, userMapper, passwordEncoder);
        when(userMapper.selectOne(any())).thenReturn(user("13800000000", passwordEncoder.encode("123456"), 1));
        when(jwtUtils.generateToken("13800000000")).thenReturn("user-token");

        LoginRequest request = loginRequest("13800000000", "123456");

        assertEquals("user-token", service.login(request));
    }

    @Test
    void loginRejectsWrongPassword() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserMapper userMapper = mock(UserMapper.class);
        AuthServiceImpl service = createService(mock(JwtUtils.class), userMapper, passwordEncoder);
        when(userMapper.selectOne(any())).thenReturn(user("13800000000", passwordEncoder.encode("123456"), 1));

        RuntimeException error = assertThrows(RuntimeException.class, () -> service.login(loginRequest("13800000000", "bad")));

        assertEquals("密码错误", error.getMessage());
    }

    @Test
    void loginRejectsDisabledUser() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserMapper userMapper = mock(UserMapper.class);
        AuthServiceImpl service = createService(mock(JwtUtils.class), userMapper, passwordEncoder);
        when(userMapper.selectOne(any())).thenReturn(user("13800000000", passwordEncoder.encode("123456"), 0));

        RuntimeException error = assertThrows(RuntimeException.class, () -> service.login(loginRequest("13800000000", "123456")));

        assertEquals("账号已被禁用", error.getMessage());
    }

    @Test
    void loginRejectsPlainStoredPassword() {
        UserMapper userMapper = mock(UserMapper.class);
        AuthServiceImpl service = createService(
                mock(JwtUtils.class),
                userMapper,
                PasswordEncoderFactories.createDelegatingPasswordEncoder()
        );
        when(userMapper.selectOne(any())).thenReturn(user("13800000000", "123456", 1));

        RuntimeException error = assertThrows(RuntimeException.class, () -> service.login(loginRequest("13800000000", "123456")));

        assertEquals("密码错误", error.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUserInfoUsesAggregateQueriesForVehicleStats() {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        UserMapper userMapper = mock(UserMapper.class);
        VehicleMapper vehicleMapper = mock(VehicleMapper.class);
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        ChargingOrderMapper chargingOrderMapper = mock(ChargingOrderMapper.class);
        VehicleMileageMapper vehicleMileageMapper = mock(VehicleMileageMapper.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthServiceImpl service = new AuthServiceImpl(
                jwtUtils,
                userMapper,
                vehicleMapper,
                userVehicleMapper,
                chargingOrderMapper,
                vehicleMileageMapper,
                passwordEncoder
        );
        User user = user("13800000000", "encoded", 1);
        user.setId(7L);
        UserVehicle firstRelation = userVehicle(7L, 20L);
        UserVehicle secondRelation = userVehicle(7L, 10L);
        Vehicle firstVehicle = vehicle(20L, "Vehicle 20");
        Vehicle secondVehicle = vehicle(10L, "Vehicle 10");
        BigDecimal totalCharged = new BigDecimal("86.50");
        BigDecimal totalMileage = new BigDecimal("3456.70");
        when(userMapper.selectOne(any())).thenReturn(user);
        when(userVehicleMapper.selectList(any())).thenReturn(List.of(firstRelation, secondRelation));
        when(vehicleMapper.selectBatchIds(List.of(20L, 10L))).thenReturn(List.of(firstVehicle, secondVehicle));
        when(chargingOrderMapper.selectTotalChargedQuantityByCarIds(List.of(20L, 10L))).thenReturn(totalCharged);
        when(vehicleMileageMapper.selectTotalMaxMileageByCarIds(List.of(20L, 10L))).thenReturn(totalMileage);

        Map<String, Object> result = service.getUserInfo("13800000000");
        Map<String, Object> userInfo = (Map<String, Object>) result.get("user");
        Map<String, Object> stats = (Map<String, Object>) userInfo.get("stats");

        assertEquals(totalCharged, stats.get("totalCharged"));
        assertEquals(totalMileage, stats.get("totalMileage"));
        assertEquals(2, stats.get("carCount"));
        verify(vehicleMapper).selectBatchIds(List.of(20L, 10L));
        verify(chargingOrderMapper).selectTotalChargedQuantityByCarIds(List.of(20L, 10L));
        verify(vehicleMileageMapper).selectTotalMaxMileageByCarIds(List.of(20L, 10L));
        verify(chargingOrderMapper, never()).selectList(any());
        verify(vehicleMileageMapper, never()).selectOne(any());
    }

    private AuthServiceImpl createService(JwtUtils jwtUtils, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        return new AuthServiceImpl(
                jwtUtils,
                userMapper,
                mock(VehicleMapper.class),
                mock(UserVehicleMapper.class),
                mock(ChargingOrderMapper.class),
                mock(VehicleMileageMapper.class),
                passwordEncoder
        );
    }

    private UserVehicle userVehicle(long userId, long carId) {
        UserVehicle relation = new UserVehicle();
        relation.setUserId(userId);
        relation.setCarId(carId);
        return relation;
    }

    private Vehicle vehicle(long id, String name) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setCarName(name);
        return vehicle;
    }

    private User user(String username, String password, int status) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setStatus(status);
        return user;
    }

    private LoginRequest loginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }
}
