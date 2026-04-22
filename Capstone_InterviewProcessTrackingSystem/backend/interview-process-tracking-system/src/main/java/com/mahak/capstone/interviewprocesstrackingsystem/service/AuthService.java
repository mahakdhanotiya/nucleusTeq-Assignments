package com.mahak.capstone.interviewprocesstrackingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.RegisterRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.Role;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    // constructor injection
    public AuthService(UserRepository userRepository,
                   PasswordEncoder passwordEncoder,
                   JwtUtil jwtUtil) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
}
    
    /**
     * Register new user
     */
    public void register(RegisterRequestDTO dto) {

        logger.info("Register attempt for email: {}", dto.getEmail());
        
        // check duplicate email
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.error("Registration failed - Email already exists: {}", dto.getEmail());
            throw new InvalidRequestException("Email already exists");
        }
        

        // create user entity
        User user = new User();
        user.setFullName(dto.getFullName().trim());
        user.setEmail(dto.getEmail().trim());

        //  encrypt password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // assign default role
        user.setRole(Role.CANDIDATE);

        // save to DB
        userRepository.save(user);

        logger.info("User registered successfully: {}", dto.getEmail());
    }


    //  Login method
    public String login(LoginRequestDTO dto) {

        logger.info("Login attempt for email: {}", dto.getEmail());

        // user fetch
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    logger.error("Login failed - User not found: {}", dto.getEmail());
                    return new ResourceNotFoundException("User not found");
                 });

        // password check
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            logger.error("Login failed - Invalid password for email: {}", dto.getEmail());
            throw new InvalidRequestException("Invalid password");
        }

        logger.info("Login successful for email: {}", user.getEmail());

        //  generate JWT token
        return jwtUtil.generateToken(user.getEmail());
    }
}
