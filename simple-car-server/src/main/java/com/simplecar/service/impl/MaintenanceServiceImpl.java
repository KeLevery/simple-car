package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplecar.model.dto.AppointmentRequest;
import com.simplecar.model.entity.*;
import com.simplecar.mapper.*;
import com.simplecar.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {
    private final MaintenancePlanMapper planMapper;
    private final MaintenanceAppointmentMapper appointmentMapper;
    private final MaintenanceAppointmentPlanMapper appointmentPlanMapper;
    private final MaintenancePayMapper payMapper;
    private final ServiceStationMapper stationMapper;
    private final UserVehicleMapper userVehicleMapper;

    public List<MaintenancePlan> getPlans() {
        return planMapper.selectList(null);
    }

    @Transactional
    public Map<String, Object> createAppointment(AppointmentRequest request) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate appointDate = LocalDate.parse(request.getAppointDateStr(), dateFormatter);

        MaintenanceAppointment appointment = new MaintenanceAppointment();
        appointment.setType(request.getType());
        appointment.setCarId(request.getCarId());

        UserVehicle userVehicle = userVehicleMapper.selectOne(
                new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getCarId, request.getCarId()).last("limit 1")
        );
        if (userVehicle != null) {
            appointment.setUserId(userVehicle.getUserId());
        }

        appointment.setCustomerName(request.getCustomerName());
        appointment.setCustomerPhone(request.getCustomerPhone());
        appointment.setAppointDate(appointDate);
        appointment.setAppointTime(request.getAppointTimeStr());
        appointment.setMaintenanceServiceStationId(request.getMaintenanceServiceStationId());
        appointment.setCustomerSignature(request.getCustomerSignature());

        String workNo = "WX" + System.currentTimeMillis();
        appointment.setWorkNo(workNo);
        appointment.setStatus(0);
        appointment.setCreatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (appointment.getType() == 0) {
            totalAmount = new BigDecimal("199.00");
        } else if (request.getPlanList() != null) {
            for (Map<String, Object> planMap : request.getPlanList()) {
                totalAmount = totalAmount.add(new BigDecimal(planMap.get("totalPrice").toString()));
            }
        }
        appointment.setTotalAmount(totalAmount);
        appointmentMapper.insert(appointment);

        if (appointment.getType() == 1 && request.getPlanList() != null) {
            for (Map<String, Object> planMap : request.getPlanList()) {
                MaintenanceAppointmentPlan detail = new MaintenanceAppointmentPlan();
                detail.setAppointmentId(appointment.getId());
                detail.setCategory(planMap.get("category").toString());
                detail.setReplacementPart(planMap.get("replacementPart").toString());
                detail.setUnitPrice(new BigDecimal(planMap.get("unitPrice").toString()));
                detail.setTotalPrice(new BigDecimal(planMap.get("totalPrice").toString()));
                detail.setDuration(Integer.valueOf(planMap.get("duration").toString()));
                appointmentPlanMapper.insert(detail);
            }
        }

        MaintenancePay pay = new MaintenancePay();
        pay.setMaintenanceAppointmentId(appointment.getId());
        pay.setPrice(totalAmount);
        pay.setStatus(0);
        pay.setCreatedAt(LocalDateTime.now());
        payMapper.insert(pay);

        Map<String, Object> result = new HashMap<>();
        result.put("id", appointment.getId());
        result.put("workNo", workNo);
        result.put("paymentId", pay.getId());
        result.put("paymentAmount", totalAmount);
        return result;
    }

    public Page<MaintenanceAppointment> getAppointmentPage(Long carId, Integer pageNum, Integer pageSize) {
        Page<MaintenanceAppointment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MaintenanceAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaintenanceAppointment::getCarId, carId);
        wrapper.orderByDesc(MaintenanceAppointment::getCreatedAt);
        appointmentMapper.selectPage(page, wrapper);

        for (MaintenanceAppointment appt : page.getRecords()) {
            List<MaintenancePay> payList = payMapper.selectList(
                    new LambdaQueryWrapper<MaintenancePay>()
                            .eq(MaintenancePay::getMaintenanceAppointmentId, appt.getId())
                            .orderByDesc(MaintenancePay::getCreatedAt));
            appt.setPayment(payList.isEmpty() ? null : payList.get(0));
        }
        return page;
    }

    public Page<ServiceStation> getStationPage(String cityId, Integer pageNum, Integer pageSize) {
        Page<ServiceStation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceStation> wrapper = new LambdaQueryWrapper<>();
        if (cityId != null && !cityId.isEmpty()) {
            wrapper.eq(ServiceStation::getCityId, cityId);
        }
        stationMapper.selectPage(page, wrapper);
        return page;
    }

    @Transactional
    public boolean updatePayStatus(Long payId, Integer status, Long appointmentId) {
        MaintenancePay pay = payMapper.selectById(payId);
        if (pay != null) {
            pay.setStatus(status);
            if (status == 1) {
                pay.setPaidAt(LocalDateTime.now());
            }
            pay.setUpdatedAt(LocalDateTime.now());
            payMapper.updateById(pay);
        }

        if (status == 1) {
            MaintenanceAppointment appointment = appointmentMapper.selectById(appointmentId);
            if (appointment != null) {
                appointment.setStatus(1);
                appointment.setUpdatedAt(LocalDateTime.now());
                appointmentMapper.updateById(appointment);
            }
        }
        return true;
    }
}
