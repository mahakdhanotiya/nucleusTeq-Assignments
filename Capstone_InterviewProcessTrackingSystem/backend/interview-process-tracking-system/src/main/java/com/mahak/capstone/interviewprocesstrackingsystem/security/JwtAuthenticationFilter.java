package com.mahak.capstone.interviewprocesstrackingsystem.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // This utility will help us extract data from JWT
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header from request
        String authHeader = request.getHeader("Authorization");

        // Check if header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Extract token by removing "Bearer "
            String token = authHeader.substring(7);

            try {
                // Extract email from token
                String email = jwtUtil.extractEmail(token);

                // If email exists → token is valid
                if (email != null) {
                    logger.info("Valid token for user: {}", email);
                }

            } catch (Exception e) {
                // If token is invalid
                logger.error("Invalid token");
            }
        }

        // Continue request flow 
        filterChain.doFilter(request, response);
    }
}