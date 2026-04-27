package com.simplecar.controller;

import com.simplecar.result.ApiResponse;
import com.simplecar.util.SecurityUtils;
import com.simplecar.model.entity.RescueRequest;
import com.simplecar.service.RescueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "救援管理")
@RestController
@RequestMapping("/rescue")
@RequiredArgsConstructor
public class RescueController {
    private final RescueService rescueService;

    @Operation(summary = "发起救援请求")
    @PostMapping("/create")
    public ApiResponse<RescueRequest> createRescue(@RequestBody RescueRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        return ApiResponse.success(rescueService.create(request, userId));
    }

    @Operation(summary = "获取救援历史")
    @GetMapping("/list")
    public ApiResponse<List<RescueRequest>> listRescues() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        return ApiResponse.success(rescueService.listByUser(userId));
    }
}
