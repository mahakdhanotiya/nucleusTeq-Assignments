package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.RegisterRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.Role;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;
import com.mahak.capstone.interviewprocesstrackingsystem.service.AuthService;

/**
 * Implementation of AuthService containing authentication logic.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(RegisterRequestDTO dto) {

        logger.info("Register attempt for email: {}", dto.getEmail());

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.error("Registration failed - Email already exists: {}", dto.getEmail());
            throw new InvalidRequestException(ErrorConstants.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setFullName(dto.getFullName().trim());
        user.setEmail(dto.getEmail().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.CANDIDATE);

        userRepository.save(user);

        logger.info("User registered successfully: {}", dto.getEmail());
    }

    @Override
    public String login(LoginRequestDTO dto) {

        logger.info("Login attempt for email: {}", dto.getEmail());

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    logger.error("Login failed - User not found: {}", dto.getEmail());
                    return new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND);
                });

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            logger.error("Login failed - Invalid password for email: {}", dto.getEmail());
            throw new InvalidRequestException(ErrorConstants.INVALID_CREDENTIALS);
        }

        logger.info("Login successful for email: {}", user.getEmail());

        return jwtUtil.generateToken(user.getEmail());
    }
}