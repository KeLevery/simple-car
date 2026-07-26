package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.VehicleMileage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface VehicleMileageMapper extends BaseMapper<VehicleMileage> {

    @Select({
            "<script>",
            "SELECT COALESCE(SUM(max_mileage), 0) FROM (",
            "SELECT MAX(car_mileage) AS max_mileage FROM vehicle_mileage WHERE car_id IN",
            "<foreach collection='carIds' item='carId' open='(' separator=',' close=')'>",
            "#{carId}",
            "</foreach>",
            "GROUP BY car_id",
            ") mileage_totals",
            "</script>"
    })
    BigDecimal selectTotalMaxMileageByCarIds(@Param("carIds") List<Long> carIds);
}
