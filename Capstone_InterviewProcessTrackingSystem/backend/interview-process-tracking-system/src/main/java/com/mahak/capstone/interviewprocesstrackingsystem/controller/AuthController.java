package com.mahak.capstone.interviewprocesstrackingsystem.controller;

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

    // constructor injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register API
    @PostMapping("/register")
    public ApiResponseDTO<Void> register(@Valid @RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return new ApiResponseDTO<>(true, "User registered successfully", null);
    }

    // login API
    @PostMapping("/login")
    public ApiResponseDTO<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {

        String token = authService.login(dto);

        LoginResponseDTO response = new LoginResponseDTO(token);


        return new ApiResponseDTO<>(true, "Login successful", response);
    }
}