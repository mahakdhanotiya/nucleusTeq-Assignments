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
import com.mahak.capstone.interviewprocesstrackingsystem.service.AuthService;

import jakarta.validation.Valid;


@RestController
@RequestMapping(ApiConstants.AUTH)
public class AuthController {

    private final AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // constructor injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
        /**
     * Handles user registration request.
     *
     * @param dto RegisterRequestDTO containing user details
     * @return ApiResponseDTO with success message
     */

    @PostMapping(ApiConstants.REGISTER)
    public ApiResponseDTO<Void> register(@Valid @RequestBody RegisterRequestDTO dto) {
        logger.info("Received register request for email: {}", dto.getEmail());
        authService.register(dto);
        logger.info("Register API completed for email: {}", dto.getEmail());

        return new ApiResponseDTO<>(true, "User registered successfully", null);
    }


        /**
     * Handles user login request.
     *
     * @param dto LoginRequestDTO containing user details
     * @return ApiResponseDTO with success message
     */

    @PostMapping(ApiConstants.LOGIN)
    public ApiResponseDTO<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        logger.info("Received login request for email: {}", dto.getEmail());
        LoginResponseDTO response = authService.login(dto);
        logger.info("Login API completed for email: {}", dto.getEmail());
        

        return new ApiResponseDTO<>(true, "Login successful", response);
    }
}