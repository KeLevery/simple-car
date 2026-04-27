package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.entity.VehicleMileage;
import com.simplecar.mapper.VehicleMileageMapper;
import com.simplecar.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {
    private final VehicleMileageMapper mileageMapper;

    public List<VehicleMileage> listMileage(Long carId) {
        return mileageMapper.selectList(
                new LambdaQueryWrapper<VehicleMileage>()
                        .eq(VehicleMileage::getCarId, carId)
                        .orderByAsc(VehicleMileage::getCreateTime)
        );
    }
}
