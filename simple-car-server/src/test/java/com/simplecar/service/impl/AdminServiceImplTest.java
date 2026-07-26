package com.simplecar.service.impl;

import com.simplecar.mapper.ChargingOrderMapper;
import com.simplecar.mapper.ChargingStationMapper;
import com.simplecar.mapper.CommunityPostMapper;
import com.simplecar.mapper.MaintenanceAppointmentMapper;
import com.simplecar.mapper.RescueRequestMapper;
import com.simplecar.mapper.ServiceStationMapper;
import com.simplecar.mapper.UserMapper;
import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.mapper.VehicleMapper;
import com.simplecar.model.entity.User;
import com.simplecar.model.entity.UserVehicle;
import com.simplecar.model.entity.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminServiceImplTest {

    private AdminServiceImpl createService(
            UserMapper userMapper,
            VehicleMapper vehicleMapper,
            UserVehicleMapper userVehicleMapper,
            MaintenanceAppointmentMapper appointmentMapper,
            ChargingOrderMapper chargingOrderMapper
    ) {
        return new AdminServiceImpl(
                userMapper,
                vehicleMapper,
                userVehicleMapper,
                appointmentMapper,
                mock(RescueRequestMapper.class),
                mock(ChargingStationMapper.class),
                mock(ServiceStationMapper.class),
                chargingOrderMapper,
                mock(CommunityPostMapper.class),
                mock(PasswordEncoder.class)
        );
    }

    @Test
    void overviewUsesDatabaseAggregatesWithoutLoadingAllOrders() {
        MaintenanceAppointmentMapper appointmentMapper = mock(MaintenanceAppointmentMapper.class);
        ChargingOrderMapper chargingOrderMapper = mock(ChargingOrderMapper.class);
        AdminServiceImpl service = createService(
                mock(UserMapper.class),
                mock(VehicleMapper.class),
                mock(UserVehicleMapper.class),
                appointmentMapper,
                chargingOrderMapper
        );
        BigDecimal chargingRevenue = new BigDecimal("128.50");
        BigDecimal maintenanceRevenue = new BigDecimal("360.00");
        when(chargingOrderMapper.selectTotalActualPaymentAmount()).thenReturn(chargingRevenue);
        when(appointmentMapper.selectTotalAmount()).thenReturn(maintenanceRevenue);
        when(appointmentMapper.selectCountByAppointDate(any(LocalDate.class))).thenReturn(3L);

        Map<String, Object> data = service.overview();

        assertEquals(chargingRevenue, data.get("chargingRevenue"));
        assertEquals(maintenanceRevenue, data.get("maintenanceRevenue"));
        assertEquals(3L, data.get("todayAppointmentCount"));
        verify(chargingOrderMapper, never()).selectList(any());
        verify(appointmentMapper, never()).selectList(any());
        verify(appointmentMapper).selectCountByAppointDate(any(LocalDate.class));
    }

    @Test
    void vehiclesLoadsRelatedUsersAndVehiclesInBatches() {
        UserMapper userMapper = mock(UserMapper.class);
        VehicleMapper vehicleMapper = mock(VehicleMapper.class);
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        AdminServiceImpl service = createService(
                userMapper,
                vehicleMapper,
                userVehicleMapper,
                mock(MaintenanceAppointmentMapper.class),
                mock(ChargingOrderMapper.class)
        );
        UserVehicle firstRelation = relation(1L, 2L, 20L, 0);
        UserVehicle secondRelation = relation(2L, 1L, 10L, 1);
        Vehicle firstVehicle = vehicle(20L, "Vehicle 20");
        Vehicle secondVehicle = vehicle(10L, "Vehicle 10");
        User firstUser = user(2L, "user-2", "User 2");
        User secondUser = user(1L, "user-1", "User 1");
        when(userVehicleMapper.selectList(any())).thenReturn(List.of(firstRelation, secondRelation));
        when(vehicleMapper.selectBatchIds(List.of(20L, 10L))).thenReturn(List.of(secondVehicle, firstVehicle));
        when(userMapper.selectBatchIds(List.of(2L, 1L))).thenReturn(List.of(secondUser, firstUser));

        List<Map<String, Object>> vehicles = service.listVehicles(null);

        assertEquals(2, vehicles.size());
        assertEquals(20L, vehicles.get(0).get("id"));
        assertEquals("user-2", vehicles.get(0).get("username"));
        assertEquals(10L, vehicles.get(1).get("id"));
        assertEquals("user-1", vehicles.get(1).get("username"));
        verify(vehicleMapper).selectBatchIds(List.of(20L, 10L));
        verify(userMapper).selectBatchIds(List.of(2L, 1L));
        verify(vehicleMapper, never()).selectById(any());
        verify(userMapper, never()).selectById(any());
    }

    @Test
    void deleteUserRemovesVehiclesInOneBatch() {
        UserMapper userMapper = mock(UserMapper.class);
        VehicleMapper vehicleMapper = mock(VehicleMapper.class);
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        AdminServiceImpl service = createService(
                userMapper,
                vehicleMapper,
                userVehicleMapper,
                mock(MaintenanceAppointmentMapper.class),
                mock(ChargingOrderMapper.class)
        );
        when(userVehicleMapper.selectList(any())).thenReturn(List.of(
                relation(1L, 7L, 101L, 0),
                relation(2L, 7L, 102L, 1)
        ));
        when(userMapper.deleteById(7L)).thenReturn(1);

        service.deleteUser(7L);

        verify(vehicleMapper).deleteBatchIds(List.of(101L, 102L));
        verify(vehicleMapper, never()).deleteById(any(Long.class));
    }

    private UserVehicle relation(long id, long userId, long carId, int isDefault) {
        UserVehicle relation = new UserVehicle();
        relation.setId(id);
        relation.setUserId(userId);
        relation.setCarId(carId);
        relation.setIsDefault(isDefault);
        return relation;
    }

    private Vehicle vehicle(long id, String name) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setCarName(name);
        return vehicle;
    }

    private User user(long id, String username, String nickName) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setNickName(nickName);
        return user;
    }
}
