package com.simplecar.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_car")
public class UserVehicle {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long carId;
    private Integer isDefault;
    private LocalDateTime createdAt;
}
