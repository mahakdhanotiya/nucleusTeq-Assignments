package com.mahak.capstone.interviewprocesstrackingsystem.service;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.RegisterRequestDTO;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     */
    void register(RegisterRequestDTO registerRequestDTO);

    /**
     * Authenticates user and returns login response.
     */
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}