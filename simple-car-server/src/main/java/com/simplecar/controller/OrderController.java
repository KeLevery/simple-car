package com.simplecar.controller;

import com.simplecar.result.ApiResponse;
import com.simplecar.result.PagedData;
import com.simplecar.result.PageResponse;
import com.simplecar.util.SecurityUtils;
import com.simplecar.model.entity.User;
import com.simplecar.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "获取用户所有订单")
    @GetMapping("/list")
    public ApiResponse<?> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            return ApiResponse.error(401, "请先登录");
        }
        PagedData<Map<String, Object>> page = orderService.getUserOrders(user.getId(), status, pageNum, pageSize);
        return PageResponse.success(page.rows(), page.total());
    }
}
