package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.entity.*;
import com.simplecar.mapper.*;
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

    public List<Map<String, Object>> getUserOrders(Long userId) {
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
