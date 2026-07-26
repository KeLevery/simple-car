package com.simplecar.component;

import com.simplecar.mapper.UserVehicleMapper;
import com.simplecar.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class OwnershipValidatorTest {

    @Test
    void allowsWhenCarBelongsToCurrentUser() {
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        OwnershipValidator validator = new OwnershipValidator(userVehicleMapper);

        when(userVehicleMapper.selectCount(any())).thenReturn(1L);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(7L);
            assertDoesNotThrow(() -> validator.requireCarOwnership(101L));
        }
    }

    @Test
    void rejectsWhenUserNotLoggedIn() {
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        OwnershipValidator validator = new OwnershipValidator(userVehicleMapper);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);
            RuntimeException error = assertThrows(RuntimeException.class, () -> validator.requireCarOwnership(101L));
            assertEquals("未登录", error.getMessage());
        }
    }

    @Test
    void rejectsWhenCarDoesNotBelongToCurrentUser() {
        UserVehicleMapper userVehicleMapper = mock(UserVehicleMapper.class);
        OwnershipValidator validator = new OwnershipValidator(userVehicleMapper);

        when(userVehicleMapper.selectCount(any())).thenReturn(0L);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(7L);
            RuntimeException error = assertThrows(RuntimeException.class, () -> validator.requireCarOwnership(101L));
            assertEquals("无权访问该车辆", error.getMessage());
        }
    }
}
