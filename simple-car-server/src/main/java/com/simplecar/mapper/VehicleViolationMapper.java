package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.VehicleViolation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VehicleViolationMapper extends BaseMapper<VehicleViolation> {
}
