package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.entity.User;
import com.simplecar.model.entity.UserSettings;
import com.simplecar.mapper.UserMapper;
import com.simplecar.mapper.UserSettingsMapper;
import com.simplecar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserSettingsMapper settingsMapper;
    private final PasswordEncoder passwordEncoder;

    public void updateProfile(User user, String nickName, String phone) {
        if (nickName != null && !nickName.trim().isEmpty()) {
            user.setNickName(nickName.trim());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone.trim());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!matchesPassword(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return true;
    }

    public Map<String, String> getSettings(Long userId, String type) {
        LambdaQueryWrapper<UserSettings> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSettings::getUserId, userId)
               .eq(UserSettings::getSettingType, type);
        List<UserSettings> settings = settingsMapper.selectList(wrapper);
        Map<String, String> result = new HashMap<>();
        for (UserSettings s : settings) {
            result.put(s.getSettingKey(), s.getSettingValue());
        }
        return result;
    }

    public void updateSettings(Long userId, String type, Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            LambdaQueryWrapper<UserSettings> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserSettings::getUserId, userId)
                   .eq(UserSettings::getSettingType, type)
                   .eq(UserSettings::getSettingKey, entry.getKey());
            UserSettings existing = settingsMapper.selectOne(wrapper);
            if (existing != null) {
                existing.setSettingValue(entry.getValue());
                existing.setUpdatedAt(LocalDateTime.now());
                settingsMapper.updateById(existing);
            } else {
                UserSettings newSetting = new UserSettings();
                newSetting.setUserId(userId);
                newSetting.setSettingType(type);
                newSetting.setSettingKey(entry.getKey());
                newSetting.setSettingValue(entry.getValue());
                settingsMapper.insert(newSetting);
            }
        }
    }

    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        try {
            if (passwordEncoder.matches(rawPassword, storedPassword)) {
                return true;
            }
        } catch (IllegalArgumentException ignored) {
            // Legacy rows without a Spring Security password id are compared below.
        }
        String plainStored = storedPassword.startsWith("{noop}") ? storedPassword.substring(6) : storedPassword;
        return plainStored.equals(rawPassword);
    }
}
