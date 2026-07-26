package com.simplecar.service.impl;

import com.simplecar.component.OwnershipValidator;
import com.simplecar.mapper.MaintenanceAppointmentMapper;
import com.simplecar.mapper.MaintenanceAppointmentPlanMapper;
import com.simplecar.mapper.MaintenancePayMapper;
import com.simplecar.mapper.MaintenancePlanMapper;
import com.simplecar.mapper.ServiceStationMapper;
import com.simplecar.model.dto.AppointmentRequest;
import com.simplecar.model.entity.MaintenanceAppointment;
import com.simplecar.model.entity.MaintenanceAppointmentPlan;
import com.simplecar.model.entity.MaintenancePay;
import com.simplecar.model.entity.MaintenancePlan;
import com.simplecar.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MaintenanceServiceImplTest {

    @Test
    void createAppointmentUsesDatabasePlanPricesInsteadOfClientPrices() {
        MaintenancePlanMapper planMapper = mock(MaintenancePlanMapper.class);
        MaintenanceAppointmentMapper appointmentMapper = mock(MaintenanceAppointmentMapper.class);
        MaintenanceAppointmentPlanMapper appointmentPlanMapper = mock(MaintenanceAppointmentPlanMapper.class);
        MaintenancePayMapper payMapper = mock(MaintenancePayMapper.class);
        ServiceStationMapper stationMapper = mock(ServiceStationMapper.class);
        OwnershipValidator ownershipValidator = mock(OwnershipValidator.class);

        MaintenanceServiceImpl service = new MaintenanceServiceImpl(
                planMapper,
                appointmentMapper,
                appointmentPlanMapper,
                payMapper,
                stationMapper,
                ownershipValidator
        );

        MaintenancePlan plan = new MaintenancePlan();
        plan.setId(11L);
        plan.setCategory("轮胎");
        plan.setReplacementPart("轮胎更换");
        plan.setUnitPrice(new BigDecimal("300.00"));
        plan.setTotalPrice(new BigDecimal("300.00"));
        plan.setDuration(60);

        when(planMapper.selectBatchIds(List.of(11L))).thenReturn(List.of(plan));
        when(appointmentMapper.insert(any(MaintenanceAppointment.class))).thenAnswer(invocation -> {
            MaintenanceAppointment appointment = invocation.getArgument(0);
            appointment.setId(88L);
            return 1;
        });
        when(payMapper.insert(any(MaintenancePay.class))).thenAnswer(invocation -> {
            MaintenancePay pay = invocation.getArgument(0);
            pay.setId(99L);
            return 1;
        });
        doNothing().when(ownershipValidator).requireCarOwnership(101L);

        AppointmentRequest request = new AppointmentRequest();
        request.setType(1);
        request.setCarId(101L);
        request.setMaintenanceServiceStationId(5L);
        request.setCustomerName("张三");
        request.setCustomerPhone("13800000000");
        request.setAppointDateStr("2026-7-26");
        request.setAppointTimeStr("10:00");
        request.setCustomerSignature("/uploads/sign.png");

        Map<String, Object> clientPlan = new HashMap<>();
        clientPlan.put("id", 11L);
        clientPlan.put("totalPrice", "0.01");
        clientPlan.put("unitPrice", "0.01");
        clientPlan.put("category", "伪造类别");
        clientPlan.put("replacementPart", "伪造配件");
        clientPlan.put("duration", 1);
        request.setPlanList(List.of(clientPlan));

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(7L);

            Map<String, Object> result = service.createAppointment(request);

            assertEquals(new BigDecimal("300.00"), result.get("paymentAmount"));
            assertEquals(99L, result.get("paymentId"));
            assertEquals(88L, result.get("id"));
        }

        ArgumentCaptor<MaintenanceAppointment> appointmentCaptor = ArgumentCaptor.forClass(MaintenanceAppointment.class);
        verify(appointmentMapper).insert(appointmentCaptor.capture());
        assertEquals(new BigDecimal("300.00"), appointmentCaptor.getValue().getTotalAmount());
        assertEquals(7L, appointmentCaptor.getValue().getUserId());

        ArgumentCaptor<MaintenanceAppointmentPlan> detailCaptor = ArgumentCaptor.forClass(MaintenanceAppointmentPlan.class);
        verify(appointmentPlanMapper).insert(detailCaptor.capture());
        assertEquals("轮胎", detailCaptor.getValue().getCategory());
        assertEquals(new BigDecimal("300.00"), detailCaptor.getValue().getTotalPrice());
        assertEquals(60, detailCaptor.getValue().getDuration());

        ArgumentCaptor<MaintenancePay> payCaptor = ArgumentCaptor.forClass(MaintenancePay.class);
        verify(payMapper).insert(payCaptor.capture());
        assertEquals(new BigDecimal("300.00"), payCaptor.getValue().getPrice());
    }

    @Test
    void updatePayStatusDerivesAppointmentFromPayRecordAndChecksOwnership() {
        MaintenancePlanMapper planMapper = mock(MaintenancePlanMapper.class);
        MaintenanceAppointmentMapper appointmentMapper = mock(MaintenanceAppointmentMapper.class);
        MaintenanceAppointmentPlanMapper appointmentPlanMapper = mock(MaintenanceAppointmentPlanMapper.class);
        MaintenancePayMapper payMapper = mock(MaintenancePayMapper.class);
        ServiceStationMapper stationMapper = mock(ServiceStationMapper.class);
        OwnershipValidator ownershipValidator = mock(OwnershipValidator.class);

        MaintenanceServiceImpl service = new MaintenanceServiceImpl(
                planMapper,
                appointmentMapper,
                appointmentPlanMapper,
                payMapper,
                stationMapper,
                ownershipValidator
        );

        MaintenancePay pay = new MaintenancePay();
        pay.setId(99L);
        pay.setMaintenanceAppointmentId(88L);
        pay.setStatus(0);

        MaintenanceAppointment appointment = new MaintenanceAppointment();
        appointment.setId(88L);
        appointment.setCarId(101L);
        appointment.setStatus(0);

        when(payMapper.selectById(99L)).thenReturn(pay);
        when(appointmentMapper.selectById(88L)).thenReturn(appointment);
        doNothing().when(ownershipValidator).requireCarOwnership(101L);

        service.updatePayStatus(99L, 1);

        verify(ownershipValidator).requireCarOwnership(101L);
        verify(payMapper).updateById(pay);
        verify(appointmentMapper).updateById(appointment);
        assertEquals(1, pay.getStatus());
        assertEquals(1, appointment.getStatus());
    }

    @Test
    void updatePayStatusRejectsMissingPayRecord() {
        MaintenancePlanMapper planMapper = mock(MaintenancePlanMapper.class);
        MaintenanceAppointmentMapper appointmentMapper = mock(MaintenanceAppointmentMapper.class);
        MaintenanceAppointmentPlanMapper appointmentPlanMapper = mock(MaintenanceAppointmentPlanMapper.class);
        MaintenancePayMapper payMapper = mock(MaintenancePayMapper.class);
        ServiceStationMapper stationMapper = mock(ServiceStationMapper.class);
        OwnershipValidator ownershipValidator = mock(OwnershipValidator.class);

        MaintenanceServiceImpl service = new MaintenanceServiceImpl(
                planMapper,
                appointmentMapper,
                appointmentPlanMapper,
                payMapper,
                stationMapper,
                ownershipValidator
        );

        when(payMapper.selectById(99L)).thenReturn(null);

        RuntimeException error = assertThrows(RuntimeException.class, () -> service.updatePayStatus(99L, 1));
        assertEquals("支付单不存在", error.getMessage());
        verify(ownershipValidator, never()).requireCarOwnership(any());
        verify(appointmentMapper, never()).updateById(any());
    }
}
