package com.mahak.capstone.interviewprocesstrackingsystem.service;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.RegisterRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.SetPasswordRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;

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

    /**
     * Sets user password using a token received via email.
     */
    void setPassword(SetPasswordRequestDTO dto);

    /**
     * Fetches details of the currently authenticated user.
     */
    User getMe();
}