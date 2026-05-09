package com.simplecar.service;

import com.simplecar.model.dto.LoginRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface AdminAuthService {
    Map<String, Object> login(LoginRequest loginRequest);

    UserDetails loadAdminUserByUsername(String username);
}
