package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.ChargingStation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChargingStationMapper extends BaseMapper<ChargingStation> {
}
