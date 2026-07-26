package com.simplecar.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.model.entity.UserVehicle;
import com.simplecar.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnershipValidator {
    private final UserVehicleMapper userVehicleMapper;

    /**
     * 校验车辆属于当前登录用户，否则抛出异常
     */
    public void requireCarOwnership(Long carId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        Long count = userVehicleMapper.selectCount(new LambdaQueryWrapper<UserVehicle>()
                .eq(UserVehicle::getUserId, userId)
                .eq(UserVehicle::getCarId, carId));
        if (count == null || count == 0) {
            throw new RuntimeException("无权访问该车辆");
        }
    }
}
