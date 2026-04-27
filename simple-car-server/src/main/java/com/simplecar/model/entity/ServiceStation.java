package com.simplecar.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("maintenance_service_station")
public class ServiceStation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String serviceStationName;
    private String cityId;
    private String address;
    private String phone;
    private Integer status;
}
