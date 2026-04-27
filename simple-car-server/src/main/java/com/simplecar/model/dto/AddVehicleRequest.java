package com.simplecar.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddVehicleRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "品牌名称不能为空")
    private String carName;

    @NotBlank(message = "车辆型号不能为空")
    private String carModels;

    @NotBlank(message = "车牌号不能为空")
    private String licenseTag;

    @NotBlank(message = "车架号不能为空")
    private String frameNumber;
}
