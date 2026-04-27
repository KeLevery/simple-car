package com.simplecar.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("rescue_request")
public class RescueRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long carId;
    private String contactName;
    private String contactPhone;
    private String location;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
