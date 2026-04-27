package com.simplecar.controller;

import com.simplecar.result.ApiResponse;
import com.simplecar.util.SecurityUtils;
import com.simplecar.model.entity.User;
import com.simplecar.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "更新个人资料")
    @PutMapping("/profile")
    public ApiResponse<Boolean> updateProfile(@RequestBody Map<String, String> params) {
        User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            return ApiResponse.error(401, "请先登录");
        }
        userService.updateProfile(user, params.get("nickName"), params.get("phone"));
        return ApiResponse.success(true);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public ApiResponse<Boolean> changePassword(@RequestBody Map<String, String> params) {
        User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            return ApiResponse.error(401, "请先登录");
        }
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return ApiResponse.error("请输入旧密码和新密码");
        }
        if (!userService.changePassword(user, oldPassword, newPassword)) {
            return ApiResponse.error("旧密码不正确");
        }
        return ApiResponse.success(true);
    }

    @Operation(summary = "获取用户设置")
    @GetMapping("/settings/{type}")
    public ApiResponse<Map<String, String>> getSettings(@PathVariable String type) {
        User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            return ApiResponse.error(401, "请先登录");
        }
        return ApiResponse.success(userService.getSettings(user.getId(), type));
    }

    @Operation(summary = "更新用户设置")
    @PutMapping("/settings/{type}")
    public ApiResponse<Boolean> updateSettings(@PathVariable String type, @RequestBody Map<String, String> params) {
        User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            return ApiResponse.error(401, "请先登录");
        }
        userService.updateSettings(user.getId(), type, params);
        return ApiResponse.success(true);
    }
}
