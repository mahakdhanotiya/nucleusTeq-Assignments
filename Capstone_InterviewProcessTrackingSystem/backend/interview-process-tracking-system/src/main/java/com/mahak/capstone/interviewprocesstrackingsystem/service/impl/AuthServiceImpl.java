package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.RegisterRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.SetPasswordRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PasswordToken;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.Role;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PasswordTokenRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;
import com.mahak.capstone.interviewprocesstrackingsystem.service.AuthService;
import com.mahak.capstone.interviewprocesstrackingsystem.service.EmailService;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Implementation of AuthService containing authentication logic.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordTokenRepository passwordTokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url:http://127.0.0.1:5500/frontend/src/pages}")
    private String frontendUrl;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           PasswordTokenRepository passwordTokenRepository,
                           EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.passwordTokenRepository = passwordTokenRepository;
        this.emailService = emailService;
    }

    /**
     * Registers a new user without password.
     * Generates a token and sends a "set password" link via email.
     *
     * @param dto user registration request data (no password)
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

        // Set a temporary random password (user will set real one via email link)
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        // Set new fields
        if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().isEmpty()) {
            user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        }
        user.setGender(dto.getGender());
        user.setMobileNumber(dto.getMobileNumber());

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

        // Generate password setup token and send email
        PasswordToken token = new PasswordToken(dto.getEmail().trim());
        passwordTokenRepository.save(token);

        String setupUrl = frontendUrl + "/set-password.html?token=" + token.getToken();
        emailService.sendPasswordSetupEmail(dto.getEmail().trim(), dto.getFullName().trim(), setupUrl);

        logger.info("User registered successfully (password pending): {}", dto.getEmail());
    }

    /**
     * Sets the user's password using a valid token from the email link.
     *
     * @param dto contains the token and new password
     * @throws InvalidRequestException if token is invalid or expired
     */
    @Override
    public void setPassword(SetPasswordRequestDTO dto) {

        logger.info("Set password request with token");

        PasswordToken token = passwordTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> {
                    logger.error("Invalid password token");
                    return new InvalidRequestException("Invalid or expired token");
                });

        if (token.isUsed()) {
            throw new InvalidRequestException("This link has already been used");
        }

        if (token.isExpired()) {
            throw new InvalidRequestException("This link has expired. Please register again.");
        }

        User user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        token.setUsed(true);
        passwordTokenRepository.save(token);

        logger.info("Password set successfully for: {}", token.getEmail());
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

    @Override
    public User getMe() {
        String email = com.mahak.capstone.interviewprocesstrackingsystem.security.CurrentUserUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND));
    }
}