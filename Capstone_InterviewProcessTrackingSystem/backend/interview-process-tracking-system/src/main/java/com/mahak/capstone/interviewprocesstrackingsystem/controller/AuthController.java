package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ApiConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.RegisterRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.SetPasswordRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.AuthService;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;

import jakarta.validation.Valid;

/**
 * REST Controller for Authentication operations.
 * Handles user registration, login, and password setup.
 */
@RestController
@RequestMapping(ApiConstants.AUTH)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles user registration (without password).
     * Sends a password setup link via email.
     * POST /auth/register
     */
    @PostMapping(ApiConstants.REGISTER)
    public ApiResponseDTO<Void> register(@Valid @RequestBody RegisterRequestDTO dto) {
        logger.info("Register request received for email: {}", dto.getEmail());
        authService.register(dto);
        logger.info("User registered successfully: {}", dto.getEmail());
        return new ApiResponseDTO<>(true, "Registration successful! Please check your email to set your password.", null);
    }

    /**
     * Handles user login.
     * POST /auth/login
     */
    @PostMapping(ApiConstants.LOGIN)
    public ApiResponseDTO<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        logger.info("Login request received for email: {}", dto.getEmail());
        LoginResponseDTO response = authService.login(dto);
        logger.info("Login successful for email: {}", dto.getEmail());
        return new ApiResponseDTO<>(true, ApiConstants.LOGIN_SUCCESS, response);
    }

    /**
     * Sets user password using a token from the email link.
     * POST /auth/set-password
     */
    @PostMapping("/set-password")
    public ApiResponseDTO<Void> setPassword(@Valid @RequestBody SetPasswordRequestDTO dto) {
        logger.info("Set password request received");
        authService.setPassword(dto);
        return new ApiResponseDTO<>(true, "Password set successfully! You can now login.", null);
    }

    /**
     * Fetches details of the currently authenticated user.
     * GET /auth/me
     */
    @org.springframework.web.bind.annotation.GetMapping("/me")
    public ApiResponseDTO<User> getMe() {
        logger.info("Get current user details request received");
        User user = authService.getMe();
        // Hide password for security
        user.setPassword("PROTECTED");
        return new ApiResponseDTO<>(true, "User details fetched", user);
    }
}