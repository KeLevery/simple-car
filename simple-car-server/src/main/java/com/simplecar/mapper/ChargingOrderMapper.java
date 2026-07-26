package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.ChargingOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ChargingOrderMapper extends BaseMapper<ChargingOrder> {

    @Select("SELECT COALESCE(SUM(actual_payment_amount), 0) FROM charging_order")
    BigDecimal selectTotalActualPaymentAmount();

    @Select({
            "<script>",
            "SELECT COALESCE(SUM(charged_quantity), 0) FROM charging_order WHERE car_id IN",
            "<foreach collection='carIds' item='carId' open='(' separator=',' close=')'>",
            "#{carId}",
            "</foreach>",
            "</script>"
    })
    BigDecimal selectTotalChargedQuantityByCarIds(@Param("carIds") List<Long> carIds);
}
