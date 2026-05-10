package com.simplecar.service.impl;

import com.simplecar.mapper.AdminRoleMapper;
import com.simplecar.mapper.AdminUserMapper;
import com.simplecar.mapper.AdminUserRoleMapper;
import com.simplecar.model.dto.LoginRequest;
import com.simplecar.model.entity.AdminRole;
import com.simplecar.model.entity.AdminUser;
import com.simplecar.model.entity.AdminUserRole;
import com.simplecar.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminAuthServiceImplTest {

    @Test
    void loginReturnsAdminTokenAndRoles() {
        AdminUserMapper adminUserMapper = mock(AdminUserMapper.class);
        AdminRoleMapper adminRoleMapper = mock(AdminRoleMapper.class);
        AdminUserRoleMapper adminUserRoleMapper = mock(AdminUserRoleMapper.class);
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        JwtUtils jwtUtils = mock(JwtUtils.class);
        AdminAuthServiceImpl service = new AdminAuthServiceImpl(
                adminUserMapper,
                adminRoleMapper,
                adminUserRoleMapper,
                passwordEncoder,
                jwtUtils
        );

        AdminUser adminUser = new AdminUser();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("123456"));
        adminUser.setStatus(1);
        AdminUserRole relation = new AdminUserRole();
        relation.setRoleId(1L);
        AdminRole role = new AdminRole();
        role.setRoleCode("ROLE_ADMIN");

        when(adminUserMapper.selectOne(any())).thenReturn(adminUser);
        when(adminUserRoleMapper.selectList(any())).thenReturn(List.of(relation));
        when(adminRoleMapper.selectBatchIds(eq(List.of(1L)))).thenReturn(List.of(role));
        when(jwtUtils.generateAdminToken("admin", List.of("ROLE_ADMIN"))).thenReturn("admin-token");

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("123456");
        Map<String, Object> result = service.login(request);

        assertEquals("admin-token", result.get("token"));
        assertEquals("admin", result.get("username"));
        assertEquals(List.of("ROLE_ADMIN"), result.get("roles"));
        ArgumentCaptor<AdminUser> captor = ArgumentCaptor.forClass(AdminUser.class);
        verify(adminUserMapper).updateById(captor.capture());
        assertEquals(1L, captor.getValue().getId());
    }

    @Test
    void loginRejectsDisabledAdminUser() {
        AdminUserMapper adminUserMapper = mock(AdminUserMapper.class);
        AdminRoleMapper adminRoleMapper = mock(AdminRoleMapper.class);
        AdminUserRoleMapper adminUserRoleMapper = mock(AdminUserRoleMapper.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        AdminAuthServiceImpl service = new AdminAuthServiceImpl(
                adminUserMapper,
                adminRoleMapper,
                adminUserRoleMapper,
                PasswordEncoderFactories.createDelegatingPasswordEncoder(),
                jwtUtils
        );

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername("admin");
        adminUser.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("123456"));
        adminUser.setStatus(0);
        when(adminUserMapper.selectOne(any())).thenReturn(adminUser);

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("123456");

        RuntimeException error = assertThrows(RuntimeException.class, () -> service.login(request));
        assertEquals("后台账号已禁用", error.getMessage());
    }

    @Test
    void loginRejectsPlainStoredPassword() {
        AdminUserMapper adminUserMapper = mock(AdminUserMapper.class);
        AdminRoleMapper adminRoleMapper = mock(AdminRoleMapper.class);
        AdminUserRoleMapper adminUserRoleMapper = mock(AdminUserRoleMapper.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        AdminAuthServiceImpl service = new AdminAuthServiceImpl(
                adminUserMapper,
                adminRoleMapper,
                adminUserRoleMapper,
                PasswordEncoderFactories.createDelegatingPasswordEncoder(),
                jwtUtils
        );

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername("admin");
        adminUser.setPassword("123456");
        adminUser.setStatus(1);
        when(adminUserMapper.selectOne(any())).thenReturn(adminUser);

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("123456");

        RuntimeException error = assertThrows(RuntimeException.class, () -> service.login(request));
        assertEquals("后台账号或密码错误", error.getMessage());
    }
}
