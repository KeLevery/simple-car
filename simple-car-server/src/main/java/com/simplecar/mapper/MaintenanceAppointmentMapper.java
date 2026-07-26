package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.MaintenanceAppointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper
public interface MaintenanceAppointmentMapper extends BaseMapper<MaintenanceAppointment> {

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM maintenance_appointment")
    BigDecimal selectTotalAmount();

    @Select("SELECT COUNT(*) FROM maintenance_appointment WHERE appoint_date = #{appointDate}")
    Long selectCountByAppointDate(@Param("appointDate") LocalDate appointDate);
}
