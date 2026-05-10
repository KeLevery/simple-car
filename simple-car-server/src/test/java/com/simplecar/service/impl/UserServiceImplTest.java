package com.simplecar.service.impl;

import com.simplecar.mapper.UserMapper;
import com.simplecar.mapper.UserSettingsMapper;
import com.simplecar.model.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class UserServiceImplTest {

    @Test
    void changePasswordAcceptsEncodedPasswordAndStoresEncodedPassword() {
        UserMapper userMapper = mock(UserMapper.class);
        UserSettingsMapper settingsMapper = mock(UserSettingsMapper.class);
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserServiceImpl service = new UserServiceImpl(userMapper, settingsMapper, passwordEncoder);

        User user = new User();
        user.setId(1L);
        user.setPassword(passwordEncoder.encode("123456"));

        assertTrue(service.changePassword(user, "123456", "new-password"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());
        String storedPassword = captor.getValue().getPassword();
        assertTrue(storedPassword.startsWith("{bcrypt}"));
        assertTrue(passwordEncoder.matches("new-password", storedPassword));
    }

    @Test
    void changePasswordRejectsPlainStoredPassword() {
        UserMapper userMapper = mock(UserMapper.class);
        UserSettingsMapper settingsMapper = mock(UserSettingsMapper.class);
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserServiceImpl service = new UserServiceImpl(userMapper, settingsMapper, passwordEncoder);

        User user = new User();
        user.setId(1L);
        user.setPassword("123456");

        assertFalse(service.changePassword(user, "123456", "new-password"));
        verifyNoInteractions(userMapper);
    }
}
