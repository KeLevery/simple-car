package com.simplecar.service.impl;

import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.mapper.VehicleMapper;
import com.simplecar.model.entity.UserVehicle;
import com.simplecar.model.entity.Vehicle;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VehicleServiceImplTest {

    @Test
    void loadsUserVehiclesWithOneBatchQuery() {
        VehicleMapper vehicleMapper = mock(VehicleMapper.class);
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        VehicleServiceImpl service = new VehicleServiceImpl(vehicleMapper, userVehicleMapper);

        UserVehicle firstRelation = userVehicle(1L, 7L, 101L);
        UserVehicle secondRelation = userVehicle(2L, 7L, 102L);
        Vehicle firstVehicle = vehicle(101L, "城市通勤车");
        Vehicle secondVehicle = vehicle(102L, "长途旅行车");

        when(userVehicleMapper.selectList(any())).thenReturn(List.of(firstRelation, secondRelation));
        when(vehicleMapper.selectBatchIds(List.of(101L, 102L))).thenReturn(List.of(secondVehicle, firstVehicle));

        List<Map<String, Object>> result = service.queryByUserId(7L);

        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).get("carID"));
        assertEquals(102L, result.get(1).get("carID"));
        verify(vehicleMapper).selectBatchIds(List.of(101L, 102L));
        verify(vehicleMapper, never()).selectById(any());
    }

    @Test
    void skipsVehicleQueryWhenUserHasNoVehicles() {
        VehicleMapper vehicleMapper = mock(VehicleMapper.class);
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        VehicleServiceImpl service = new VehicleServiceImpl(vehicleMapper, userVehicleMapper);

        when(userVehicleMapper.selectList(any())).thenReturn(List.of());

        assertEquals(List.of(), service.queryByUserId(7L));
        verify(vehicleMapper, never()).selectBatchIds(any());
    }

    private UserVehicle userVehicle(Long id, Long userId, Long carId) {
        UserVehicle relation = new UserVehicle();
        relation.setId(id);
        relation.setUserId(userId);
        relation.setCarId(carId);
        return relation;
    }

    private Vehicle vehicle(Long id, String name) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setCarName(name);
        return vehicle;
    }
}
