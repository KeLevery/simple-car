package com.simplecar.controller;

import com.simplecar.result.ApiResponse;
import com.simplecar.model.dto.LoginRequest;
import com.simplecar.service.AuthService;
import com.simplecar.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest);
            Map<String, Object> data = Map.of("token", token);
            return ApiResponse.success("登录成功", data);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage() != null ? e.getMessage() : "用户名或密码错误");
        }
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody Map<String, String> params) {
        try {
            authService.register(
                    params.get("username"),
                    params.get("password"),
                    params.get("nickName"),
                    params.get("phone")
            );
            return ApiResponse.success("注册成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage() != null ? e.getMessage() : "注册失败");
        }
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/getInfo")
    public ApiResponse<Map<String, Object>> getInfo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtUtils.extractUsername(token);
        } catch (Exception e) {
            return ApiResponse.error(401, "登录已过期");
        }
        if (username == null) {
            return ApiResponse.error(401, "未登录");
        }
        Map<String, Object> result = authService.getUserInfo(username);
        if (result == null) {
            return ApiResponse.error("用户不存在");
        }
        return ApiResponse.success("查询成功", result);
    }
}
