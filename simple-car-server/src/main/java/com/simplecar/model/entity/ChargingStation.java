package com.simplecar.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("charging_station")
public class ChargingStation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String stationName;
    private String address;
    private String cityId;
    private Integer totalPiles;
    private Integer availablePiles;
    private Integer status;
    private LocalDateTime createTime;
}
