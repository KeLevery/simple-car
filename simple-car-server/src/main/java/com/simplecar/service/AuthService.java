package com.simplecar.service;

import com.simplecar.model.dto.LoginRequest;

import java.util.Map;

public interface AuthService {
    String login(LoginRequest loginRequest);

    Map<String, Object> getUserInfo(String username);

    void register(String username, String password, String nickName, String phone);
}
