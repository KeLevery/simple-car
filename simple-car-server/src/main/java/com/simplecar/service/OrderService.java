package com.simplecar.service;

import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Map<String, Object>> getUserOrders(Long userId);
}
