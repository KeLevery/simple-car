package com.simplecar.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppointmentRequest {
    @NotNull(message = "预约类型不能为空")
    private Integer type;

    @NotNull(message = "车辆ID不能为空")
    private Long carId;

    @NotNull(message = "服务站ID不能为空")
    private Long maintenanceServiceStationId;

    @NotBlank(message = "客户姓名不能为空")
    private String customerName;

    @NotBlank(message = "联系电话不能为空")
    private String customerPhone;

    @NotBlank(message = "预约日期不能为空")
    private String appointDateStr;

    @NotBlank(message = "预约时间不能为空")
    private String appointTimeStr;

    private String customerSignature;

    private List<Map<String, Object>> planList;
}
