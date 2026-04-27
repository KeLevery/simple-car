package com.simplecar.service;

import com.simplecar.model.entity.User;

import java.util.Map;

public interface UserService {
    void updateProfile(User user, String nickName, String phone);

    boolean changePassword(User user, String oldPassword, String newPassword);

    Map<String, String> getSettings(Long userId, String type);

    void updateSettings(Long userId, String type, Map<String, String> params);
}
