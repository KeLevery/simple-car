package com.simplecar.service.impl;

import com.simplecar.mapper.ChargingOrderMapper;
import com.simplecar.mapper.UserMapper;
import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.mapper.VehicleMapper;
import com.simplecar.mapper.VehicleMileageMapper;
import com.simplecar.model.dto.LoginRequest;
import com.simplecar.model.entity.User;
import com.simplecar.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
