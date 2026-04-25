package com.mahak.capstone.interviewprocesstrackingsystem.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // This utility will help us extract data from JWT
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
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
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null)  {

                    // Fetch user from DB
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    // Get role from DB
                    String role = user.getRole().name();

                    // Create authentication object with role
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                   user,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            );

                    // Set authentication in Spring Security context
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    log.info("User authenticated: {} with role: {}", user.getEmail() ,role);
                }

         } catch (Exception e) {
            // If token is invalid
            log.error("Invalid token: {}", e.getMessage());
        }
     }

        // Continue request flow 
        filterChain.doFilter(request, response);
    }
}