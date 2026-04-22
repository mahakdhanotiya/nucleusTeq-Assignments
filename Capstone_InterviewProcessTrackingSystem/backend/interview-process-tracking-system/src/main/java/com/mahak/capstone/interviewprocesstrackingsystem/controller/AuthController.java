package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.RegisterRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.AuthService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // constructor injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register API
    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ApiResponseDTO<Void> register(@Valid @RequestBody RegisterRequestDTO dto) {

        logger.info("Received register request for email: {}", dto.getEmail());

        authService.register(dto);

        logger.info("Register API completed for email: {}", dto.getEmail());

        return new ApiResponseDTO<>(true, "User registered successfully", null);
    }


    // login API
    @PostMapping("/login")
    public ApiResponseDTO<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {

        logger.info("Received login request for email: {}", dto.getEmail());

        String token = authService.login(dto);

        logger.info("Login API completed for email: {}", dto.getEmail());

        LoginResponseDTO response = new LoginResponseDTO(token);


        return new ApiResponseDTO<>(true, "Login successful", response);
    }
}