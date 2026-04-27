package com.simplecar.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.entity.User;
import com.simplecar.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SecurityUtils {

    private static UserMapper userMapper;

    private static JwtUtils jwtUtils;

    public SecurityUtils(UserMapper userMapper, JwtUtils jwtUtils) {
        SecurityUtils.userMapper = userMapper;
        SecurityUtils.jwtUtils = jwtUtils;
    }

    public static String getCurrentUsername() {
        // 优先从 SecurityContext 获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        // 回退：从请求头 JWT 解析
        return getUsernameFromJwt();
    }

    private static String getUsernameFromJwt() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest request = attrs.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return jwtUtils.extractUsername(token);
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    public static User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
    }

    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}
