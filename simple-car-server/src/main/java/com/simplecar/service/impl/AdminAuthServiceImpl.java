package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.mapper.AdminRoleMapper;
import com.simplecar.mapper.AdminUserMapper;
import com.simplecar.mapper.AdminUserRoleMapper;
import com.simplecar.model.dto.LoginRequest;
import com.simplecar.model.entity.AdminRole;
import com.simplecar.model.entity.AdminUser;
import com.simplecar.model.entity.AdminUserRole;
import com.simplecar.service.AdminAuthService;
import com.simplecar.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {
    private final AdminUserMapper adminUserMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final AdminUserRoleMapper adminUserRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public Map<String, Object> login(LoginRequest loginRequest) {
        AdminUser adminUser = findByUsername(loginRequest.getUsername());
        if (adminUser == null) {
            throw new RuntimeException("后台账号不存在");
        }
        if (adminUser.getStatus() == null || adminUser.getStatus() == 0) {
            throw new RuntimeException("后台账号已禁用");
        }
        if (!matchesPassword(loginRequest.getPassword(), adminUser.getPassword())) {
            throw new RuntimeException("后台账号或密码错误");
        }

        List<String> roles = loadRoleCodes(adminUser.getId());
        if (roles.isEmpty()) {
            throw new RuntimeException("后台账号未分配角色");
        }

        AdminUser update = new AdminUser();
        update.setId(adminUser.getId());
        update.setLastLoginAt(LocalDateTime.now());
        adminUserMapper.updateById(update);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", jwtUtils.generateAdminToken(adminUser.getUsername(), roles));
        data.put("username", adminUser.getUsername());
        data.put("roles", roles);
        return data;
    }

    @Override
    public UserDetails loadAdminUserByUsername(String username) {
        AdminUser adminUser = findByUsername(username);
        if (adminUser == null) {
            throw new UsernameNotFoundException("Admin user not found: " + username);
        }
        List<String> roles = loadRoleCodes(adminUser.getId());
        return org.springframework.security.core.userdetails.User.builder()
                .username(adminUser.getUsername())
                .password(adminUser.getPassword())
                .disabled(adminUser.getStatus() == null || adminUser.getStatus() == 0)
                .authorities(roles.toArray(String[]::new))
                .build();
    }

    private AdminUser findByUsername(String username) {
        return adminUserMapper.selectOne(
                new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, username)
        );
    }

    private List<String> loadRoleCodes(Long adminUserId) {
        List<AdminUserRole> relations = adminUserRoleMapper.selectList(
                new LambdaQueryWrapper<AdminUserRole>().eq(AdminUserRole::getAdminUserId, adminUserId)
        );
        if (relations.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = relations.stream().map(AdminUserRole::getRoleId).toList();
        return adminRoleMapper.selectBatchIds(roleIds).stream()
                .map(AdminRole::getRoleCode)
                .filter(role -> role != null && !role.isBlank())
                .map(this::normalizeRole)
                .distinct()
                .toList();
    }

    private String normalizeRole(String roleCode) {
        return roleCode.startsWith("ROLE_") ? roleCode : "ROLE_" + roleCode;
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
