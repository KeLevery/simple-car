package com.simplecar.service;

import com.simplecar.result.PagedData;

import java.util.Map;

public interface OrderService {
    PagedData<Map<String, Object>> getUserOrders(Long userId, Integer pageNum, Integer pageSize);
}
