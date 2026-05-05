package com.mahak.capstone.interviewprocesstrackingsystem.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "verysecretkeyforjwttestingpurposeonly12345");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    void testGenerateAndExtractToken() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email, "HR");
        
        assertNotNull(token);
        String extractedEmail = jwtUtil.extractEmail(token);
        assertEquals(email, extractedEmail);
    }
}
