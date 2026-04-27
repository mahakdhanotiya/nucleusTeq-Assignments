package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginResponseDTO;
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

    /**
     * Registers a new user with role-based validation.
     *
     * @param dto user registration request data
     * @throws InvalidRequestException if user already exists or invalid role
     */

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

        // Determine role: only emails with ".hr@" pattern can be HR
        String email = dto.getEmail().trim().toLowerCase();
        Role role;
        if (email.contains(".hr@")) {
            role = Role.HR;
        } else if (dto.getRole() != null && dto.getRole() == Role.HR) {
            logger.error("HR registration denied for non-HR email: {}", email);
            throw new InvalidRequestException("Only emails with '.hr@' pattern can register as HR");
        } else {
            role = (dto.getRole() != null) ? dto.getRole() : Role.CANDIDATE;
        }
        user.setRole(role);

        userRepository.save(user);

        logger.info("User registered successfully: {}", dto.getEmail());
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param dto login request data
     * @return LoginResponseDTO containing the generated token and user details
     * @throws ResourceNotFoundException if user is not found
     * @throws InvalidRequestException if credentials are invalid
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

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
        String role = user.getRole().name();
         // generate token
        String token = jwtUtil.generateToken(user.getEmail(), role);
    

        return new LoginResponseDTO(token, role, user.getId());
    }
}