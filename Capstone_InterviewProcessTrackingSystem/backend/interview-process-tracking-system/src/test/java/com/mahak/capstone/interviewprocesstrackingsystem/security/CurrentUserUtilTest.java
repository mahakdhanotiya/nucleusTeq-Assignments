package com.mahak.capstone.interviewprocesstrackingsystem.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;

public class CurrentUserUtilTest {

    @Test
    void testGetCurrentUserEmail_Authenticated() {
        User user = new User();
        user.setEmail("test@example.com");
        
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(user);
        
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        
        assertEquals("test@example.com", CurrentUserUtil.getCurrentUserEmail());
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserEmail_NotAuthenticated() {
        SecurityContextHolder.clearContext();
        assertNull(CurrentUserUtil.getCurrentUserEmail());
    }
}
