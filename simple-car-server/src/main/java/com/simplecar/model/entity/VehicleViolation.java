package com.simplecar.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("vehicle_violation")
public class VehicleViolation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long carId;
    private String violationType;
    private String location;
    private BigDecimal fineAmount;
    private Integer deductPoints;
    private Integer status;
    private LocalDateTime violationTime;
    private LocalDateTime createTime;
}
