package com.simplecar.controller;

import com.simplecar.model.dto.LoginRequest;
import com.simplecar.result.ApiResponse;
import com.simplecar.service.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "后台认证")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AdminAuthService adminAuthService;

    @Operation(summary = "后台登录")
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            return ApiResponse.success("登录成功", adminAuthService.login(loginRequest));
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage() != null ? e.getMessage() : "后台登录失败");
        }
    }
}
