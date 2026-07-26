package com.simplecar.controller;

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
import com.simplecar.result.ApiResponse;
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

class AdminControllerTest {

    @Test
    void overviewUsesDatabaseAggregatesWithoutLoadingAllOrders() {
        UserMapper userMapper = mock(UserMapper.class);
        VehicleMapper vehicleMapper = mock(VehicleMapper.class);
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        MaintenanceAppointmentMapper appointmentMapper = mock(MaintenanceAppointmentMapper.class);
        RescueRequestMapper rescueRequestMapper = mock(RescueRequestMapper.class);
        ChargingStationMapper chargingStationMapper = mock(ChargingStationMapper.class);
        ServiceStationMapper serviceStationMapper = mock(ServiceStationMapper.class);
        ChargingOrderMapper chargingOrderMapper = mock(ChargingOrderMapper.class);
        CommunityPostMapper communityPostMapper = mock(CommunityPostMapper.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AdminController controller = new AdminController(
                userMapper,
                vehicleMapper,
                userVehicleMapper,
                appointmentMapper,
                rescueRequestMapper,
                chargingStationMapper,
                serviceStationMapper,
                chargingOrderMapper,
                communityPostMapper,
                passwordEncoder
        );
        BigDecimal chargingRevenue = new BigDecimal("128.50");
        BigDecimal maintenanceRevenue = new BigDecimal("360.00");
        when(chargingOrderMapper.selectTotalActualPaymentAmount()).thenReturn(chargingRevenue);
        when(appointmentMapper.selectTotalAmount()).thenReturn(maintenanceRevenue);
        when(appointmentMapper.selectCountByAppointDate(any(LocalDate.class))).thenReturn(3L);

        ApiResponse<Map<String, Object>> response = controller.overview();

        assertEquals(chargingRevenue, response.getData().get("chargingRevenue"));
        assertEquals(maintenanceRevenue, response.getData().get("maintenanceRevenue"));
        assertEquals(3L, response.getData().get("todayAppointmentCount"));
        verify(chargingOrderMapper, never()).selectList(any());
        verify(appointmentMapper, never()).selectList(any());
        verify(appointmentMapper).selectCountByAppointDate(any(LocalDate.class));
    }
    @Test
    void vehiclesLoadsRelatedUsersAndVehiclesInBatches() {
        UserMapper userMapper = mock(UserMapper.class);
        VehicleMapper vehicleMapper = mock(VehicleMapper.class);
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        MaintenanceAppointmentMapper appointmentMapper = mock(MaintenanceAppointmentMapper.class);
        RescueRequestMapper rescueRequestMapper = mock(RescueRequestMapper.class);
        ChargingStationMapper chargingStationMapper = mock(ChargingStationMapper.class);
        ServiceStationMapper serviceStationMapper = mock(ServiceStationMapper.class);
        ChargingOrderMapper chargingOrderMapper = mock(ChargingOrderMapper.class);
        CommunityPostMapper communityPostMapper = mock(CommunityPostMapper.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AdminController controller = new AdminController(
                userMapper,
                vehicleMapper,
                userVehicleMapper,
                appointmentMapper,
                rescueRequestMapper,
                chargingStationMapper,
                serviceStationMapper,
                chargingOrderMapper,
                communityPostMapper,
                passwordEncoder
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

        ApiResponse<List<Map<String, Object>>> response = controller.vehicles(null);

        assertEquals(2, response.getData().size());
        assertEquals(20L, response.getData().get(0).get("id"));
        assertEquals("user-2", response.getData().get(0).get("username"));
        assertEquals(10L, response.getData().get(1).get("id"));
        assertEquals("user-1", response.getData().get(1).get("username"));
        verify(vehicleMapper).selectBatchIds(List.of(20L, 10L));
        verify(userMapper).selectBatchIds(List.of(2L, 1L));
        verify(vehicleMapper, never()).selectById(any());
        verify(userMapper, never()).selectById(any());
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
