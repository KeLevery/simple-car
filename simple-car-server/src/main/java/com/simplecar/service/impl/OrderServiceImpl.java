package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.entity.*;
import com.simplecar.mapper.*;
import com.simplecar.result.PagedData;
import com.simplecar.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ChargingOrderMapper chargingOrderMapper;
    private final MaintenancePayMapper maintenancePayMapper;
    private final MaintenanceAppointmentMapper appointmentMapper;
    private final UserVehicleMapper userVehicleMapper;

    /**
     * 充电+维保订单跨两张异构表合并排序，无法直接 selectPage，采用内存分页。
     * 单用户订单量级为几十~几百条，成本可忽略；若量级显著增长，迁移方向是
     * UNION ALL 自定义 SQL + 数据库分页。
     */
    @Override
    public PagedData<Map<String, Object>> getUserOrders(Long userId, String status, Integer pageNum, Integer pageSize) {
        List<Map<String, Object>> allOrders = loadAllOrders(userId);
        if (status != null && !status.isBlank()) {
            allOrders = allOrders.stream()
                    .filter(order -> status.equals(order.get("status")))
                    .collect(Collectors.toList());
        }
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null ? 10 : Math.max(1, Math.min(pageSize, 50));
        int total = allOrders.size();
        int from = Math.min((num - 1) * size, total);
        int to = Math.min(from + size, total);
        return new PagedData<>(allOrders.subList(from, to), total);
    }

    private List<Map<String, Object>> loadAllOrders(Long userId) {
        List<Map<String, Object>> allOrders = new ArrayList<>();

        List<UserVehicle> userVehicles = userVehicleMapper.selectList(
                new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getUserId, userId));
        List<Long> carIds = userVehicles.stream().map(UserVehicle::getCarId).collect(Collectors.toList());
        if (!carIds.isEmpty()) {
            List<ChargingOrder> chargingOrders = chargingOrderMapper.selectList(
                    new LambdaQueryWrapper<ChargingOrder>().in(ChargingOrder::getCarId, carIds).orderByDesc(ChargingOrder::getCreateTime));
            for (ChargingOrder order : chargingOrders) {
                Map<String, Object> map = new HashMap<>();
                map.put("uid", "charge-" + order.getId());
                map.put("id", order.getId());
                map.put("type", "充电订单");
                map.put("amount", order.getActualPaymentAmount());
                map.put("time", order.getCreateTime());
                map.put("status", "已支付");
                map.put("detail", order.getChargedQuantity() + " kWh");
                allOrders.add(map);
            }
        }

        LambdaQueryWrapper<MaintenanceAppointment> appointmentWrapper = new LambdaQueryWrapper<>();
        appointmentWrapper.eq(MaintenanceAppointment::getUserId, userId);
        if (!carIds.isEmpty()) {
            appointmentWrapper.or().in(MaintenanceAppointment::getCarId, carIds);
        }
        List<MaintenanceAppointment> appointments = appointmentMapper.selectList(appointmentWrapper);
        if (appointments.isEmpty()) {
            allOrders.sort((a, b) -> ((LocalDateTime) b.get("time")).compareTo((LocalDateTime) a.get("time")));
            return allOrders;
        }

        Map<Long, MaintenanceAppointment> appointmentById = appointments.stream()
                .collect(Collectors.toMap(MaintenanceAppointment::getId, appointment -> appointment));
        List<Long> appointmentIds = appointments.stream().map(MaintenanceAppointment::getId).collect(Collectors.toList());
        List<MaintenancePay> payList = maintenancePayMapper.selectList(
                new LambdaQueryWrapper<MaintenancePay>()
                        .in(MaintenancePay::getMaintenanceAppointmentId, appointmentIds)
                        .orderByDesc(MaintenancePay::getCreatedAt));
        for (MaintenancePay pay : payList) {
            MaintenanceAppointment appointment = appointmentById.get(pay.getMaintenanceAppointmentId());
            Map<String, Object> map = new HashMap<>();
            map.put("uid", "maint-" + pay.getId());
            map.put("id", pay.getId());
            map.put("type", "维保订单");
            map.put("amount", pay.getPrice());
            map.put("time", pay.getCreatedAt());
            map.put("status", pay.getStatus() == 1 ? "已支付" : (pay.getStatus() == 0 ? "待支付" : "已取消"));
            map.put("detail", appointment != null && appointment.getWorkNo() != null ? appointment.getWorkNo() : "maintenance service");
            allOrders.add(map);
        }

        allOrders.sort((a, b) -> ((LocalDateTime) b.get("time")).compareTo((LocalDateTime) a.get("time")));
        return allOrders;
    }
}
