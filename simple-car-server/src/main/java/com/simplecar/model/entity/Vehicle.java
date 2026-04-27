package com.simplecar.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("car")
public class Vehicle {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String carName;
    private String carModels;
    private String licenseTag;
    private String frameNumber;
    private BigDecimal enduranceMileage;
    private Integer remainingPower;
    private BigDecimal temperature;
    private Integer carState;
    private LocalDateTime updatedAt;
}
