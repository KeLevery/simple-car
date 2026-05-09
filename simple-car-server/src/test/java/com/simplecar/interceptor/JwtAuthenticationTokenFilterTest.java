package com.simplecar.interceptor;

import com.simplecar.service.AdminAuthService;
import com.simplecar.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationTokenFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesAdminTokenWithAdminRole() throws Exception {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        AdminAuthService adminAuthService = mock(AdminAuthService.class);
        JwtAuthenticationTokenFilter filter = new JwtAuthenticationTokenFilter(
                jwtUtils,
                userDetailsService,
                adminAuthService
        );
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        UserDetails adminDetails = User.withUsername("admin")
                .password("{noop}123456")
                .authorities("ROLE_ADMIN")
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer admin-token");
        when(jwtUtils.extractUsername("admin-token")).thenReturn("admin");
        when(jwtUtils.extractAccountType("admin-token")).thenReturn(JwtUtils.ACCOUNT_TYPE_ADMIN);
        when(adminAuthService.loadAdminUserByUsername("admin")).thenReturn(adminDetails);
        when(jwtUtils.validateToken("admin-token", "admin")).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin", SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals(
                "ROLE_ADMIN",
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority()
        );
        verify(chain).doFilter(request, response);
    }
}
