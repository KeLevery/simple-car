package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.ServiceStation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceStationMapper extends BaseMapper<ServiceStation> {
}
