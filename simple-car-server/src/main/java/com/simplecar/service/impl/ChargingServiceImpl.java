package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.entity.ChargingOrder;
import com.simplecar.model.entity.ChargingStation;
import com.simplecar.mapper.ChargingOrderMapper;
import com.simplecar.mapper.ChargingStationMapper;
import com.simplecar.service.ChargingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingServiceImpl implements ChargingService {
    private final ChargingStationMapper stationMapper;
    private final ChargingOrderMapper orderMapper;

    public List<ChargingStation> listByCity(String cityId, Boolean availableOnly) {
        LambdaQueryWrapper<ChargingStation> wrapper = new LambdaQueryWrapper<>();
        if (cityId != null && !cityId.isEmpty()) {
            wrapper.eq(ChargingStation::getCityId, cityId);
        }
        if (Boolean.TRUE.equals(availableOnly)) {
            wrapper.gt(ChargingStation::getAvailablePiles, 0);
        }
        wrapper.eq(ChargingStation::getStatus, 1);
        return stationMapper.selectList(wrapper);
    }

    public List<ChargingOrder> listOrders(Long carId) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<ChargingOrder>()
                        .eq(ChargingOrder::getCarId, carId)
                        .orderByAsc(ChargingOrder::getCreateTime)
        );
    }
}
