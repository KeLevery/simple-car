package com.simplecar.service.impl;

import com.simplecar.mapper.ChargingOrderMapper;
import com.simplecar.mapper.MaintenanceAppointmentMapper;
import com.simplecar.mapper.MaintenancePayMapper;
import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.model.entity.MaintenanceAppointment;
import com.simplecar.model.entity.UserVehicle;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Test
    void doesNotLoadMaintenancePaymentsWhenCurrentUserHasNoAppointments() {
        ChargingOrderMapper chargingOrderMapper = mock(ChargingOrderMapper.class);
        MaintenancePayMapper maintenancePayMapper = mock(MaintenancePayMapper.class);
        MaintenanceAppointmentMapper appointmentMapper = mock(MaintenanceAppointmentMapper.class);
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        OrderServiceImpl service = new OrderServiceImpl(
                chargingOrderMapper,
                maintenancePayMapper,
                appointmentMapper,
                userVehicleMapper
        );

        UserVehicle vehicle = new UserVehicle();
        vehicle.setUserId(1L);
        vehicle.setCarId(10L);
        when(userVehicleMapper.selectList(any())).thenReturn(Collections.singletonList(vehicle));
        when(chargingOrderMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(appointmentMapper.selectList(any())).thenReturn(Collections.emptyList());

        assertTrue(service.getUserOrders(1L).isEmpty());
        verify(maintenancePayMapper, never()).selectList(any());
    }

    @Test
    void loadsMaintenancePaymentsOnlyAfterFindingUserAppointments() {
        ChargingOrderMapper chargingOrderMapper = mock(ChargingOrderMapper.class);
        MaintenancePayMapper maintenancePayMapper = mock(MaintenancePayMapper.class);
        MaintenanceAppointmentMapper appointmentMapper = mock(MaintenanceAppointmentMapper.class);
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        OrderServiceImpl service = new OrderServiceImpl(
                chargingOrderMapper,
                maintenancePayMapper,
                appointmentMapper,
                userVehicleMapper
        );

        UserVehicle vehicle = new UserVehicle();
        vehicle.setUserId(1L);
        vehicle.setCarId(10L);
        MaintenanceAppointment appointment = new MaintenanceAppointment();
        appointment.setId(100L);
        appointment.setUserId(1L);
        appointment.setCarId(10L);

        when(userVehicleMapper.selectList(any())).thenReturn(Collections.singletonList(vehicle));
        when(chargingOrderMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(appointmentMapper.selectList(any())).thenReturn(Collections.singletonList(appointment));
        when(maintenancePayMapper.selectList(any())).thenReturn(Collections.emptyList());

        assertTrue(service.getUserOrders(1L).isEmpty());
        verify(maintenancePayMapper).selectList(any());
    }
}
