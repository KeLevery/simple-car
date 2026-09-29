package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplecar.component.OwnershipValidator;
import com.simplecar.model.dto.AppointmentRequest;
import com.simplecar.model.entity.MaintenanceAppointment;
import com.simplecar.model.entity.MaintenanceAppointmentPlan;
import com.simplecar.model.entity.MaintenancePay;
import com.simplecar.model.entity.MaintenancePlan;
import com.simplecar.model.entity.ServiceStation;
import com.simplecar.mapper.MaintenanceAppointmentMapper;
import com.simplecar.mapper.MaintenanceAppointmentPlanMapper;
import com.simplecar.mapper.MaintenancePayMapper;
import com.simplecar.mapper.MaintenancePlanMapper;
import com.simplecar.mapper.ServiceStationMapper;
import com.simplecar.service.MaintenanceService;
import com.simplecar.util.SecurityUtils;
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
    private final OwnershipValidator ownershipValidator;

    public List<MaintenancePlan> getPlans() {
        return planMapper.selectList(null);
    }

    @Transactional
    public Map<String, Object> createAppointment(AppointmentRequest request) {
        ownershipValidator.requireCarOwnership(request.getCarId());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate appointDate = LocalDate.parse(request.getAppointDateStr(), dateFormatter);

        MaintenanceAppointment appointment = new MaintenanceAppointment();
        appointment.setType(request.getType());
        appointment.setCarId(request.getCarId());
        appointment.setUserId(SecurityUtils.getCurrentUserId());

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

        // 金额以数据库中的维保计划价格为准，忽略客户端传入的价格
        List<MaintenancePlan> plans = Collections.emptyList();
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (appointment.getType() == 0) {
            totalAmount = new BigDecimal("199.00");
        } else if (request.getPlanList() != null && !request.getPlanList().isEmpty()) {
            List<Long> planIds = new ArrayList<>();
            for (Map<String, Object> planMap : request.getPlanList()) {
                Object id = planMap.get("id");
                if (id == null) {
                    throw new RuntimeException("维保计划不存在");
                }
                planIds.add(Long.valueOf(id.toString()));
            }
            plans = planMapper.selectBatchIds(planIds);
            if (plans.size() != planIds.size()) {
                throw new RuntimeException("维保计划不存在");
            }
            for (MaintenancePlan plan : plans) {
                totalAmount = totalAmount.add(plan.getTotalPrice());
            }
        }
        appointment.setTotalAmount(totalAmount);
        appointmentMapper.insert(appointment);

        if (appointment.getType() == 1) {
            for (MaintenancePlan plan : plans) {
                MaintenanceAppointmentPlan detail = new MaintenanceAppointmentPlan();
                detail.setAppointmentId(appointment.getId());
                detail.setCategory(plan.getCategory());
                detail.setReplacementPart(plan.getReplacementPart());
                detail.setUnitPrice(plan.getUnitPrice());
                detail.setTotalPrice(plan.getTotalPrice());
                detail.setDuration(plan.getDuration());
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
    public boolean updatePayStatus(Long payId, Integer status) {
        MaintenancePay pay = payMapper.selectById(payId);
        if (pay == null) {
            throw new RuntimeException("支付单不存在");
        }

        Long appointmentId = pay.getMaintenanceAppointmentId();
        if (appointmentId == null) {
            throw new RuntimeException("支付单关联预约不存在");
        }

        MaintenanceAppointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("支付单关联预约不存在");
        }

        // 以支付单关联的预约车辆为准做归属校验，不信任客户端传的 appointmentId
        ownershipValidator.requireCarOwnership(appointment.getCarId());

        // 状态机守卫：0未支付 1已支付 2已取消，已支付/已取消均为终态
        if (status == null || status < 0 || status > 2) {
            throw new RuntimeException("非法的支付状态");
        }
        Integer current = pay.getStatus();
        if (current != null && current.equals(status)) {
            return true; // 幂等：重复提交同一状态直接成功
        }
        if (current != null && current == 1) {
            throw new RuntimeException("订单已支付，不能重复操作");
        }
        if (current != null && current == 2) {
            throw new RuntimeException("订单已取消，不能修改状态");
        }

        pay.setStatus(status);
        if (status == 1) {
            pay.setPaidAt(LocalDateTime.now());
        }
        pay.setUpdatedAt(LocalDateTime.now());
        payMapper.updateById(pay);

        // 支付成功仅将待处理(0)的预约推进到处理中(1)，不回退已完成/已取消的预约
        if (status == 1 && appointment.getStatus() != null && appointment.getStatus() == 0) {
            appointment.setStatus(1);
            appointment.setUpdatedAt(LocalDateTime.now());
            appointmentMapper.updateById(appointment);
        }
        return true;
    }
}
