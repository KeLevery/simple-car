package com.simplecar.controller;

import com.simplecar.result.ApiResponse;
import com.simplecar.util.SecurityUtils;
import com.simplecar.service.ViolationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "违章管理")
@RestController
@RequestMapping("/violation")
@RequiredArgsConstructor
public class ViolationController {
    private final ViolationService violationService;

    @Operation(summary = "查询违章记录")
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getViolations(@RequestParam(required = false) Long carId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        return ApiResponse.success(violationService.getViolations(userId, carId));
    }
}
