package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.UserVehicle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserVehicleMapper extends BaseMapper<UserVehicle> {
}
