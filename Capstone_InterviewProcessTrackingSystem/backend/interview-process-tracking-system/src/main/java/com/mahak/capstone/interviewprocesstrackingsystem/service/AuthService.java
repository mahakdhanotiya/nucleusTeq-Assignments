package com.mahak.capstone.interviewprocesstrackingsystem.service;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.RegisterRequestDTO;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {

    void register(RegisterRequestDTO registerRequestDTO);

    String login(LoginRequestDTO loginRequestDTO);
}