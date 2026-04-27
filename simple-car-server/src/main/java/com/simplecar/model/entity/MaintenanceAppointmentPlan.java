package com.simplecar.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("maintenance_appointment_plan")
public class MaintenanceAppointmentPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long appointmentId;
    private String category;
    private String replacementPart;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Integer duration;
}
