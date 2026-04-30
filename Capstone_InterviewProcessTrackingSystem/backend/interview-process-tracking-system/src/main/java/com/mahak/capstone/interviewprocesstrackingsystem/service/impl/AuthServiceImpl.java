package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.mahak.capstone.interviewprocesstrackingsystem.repository.CandidateRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PanelProfileRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;
import com.mahak.capstone.interviewprocesstrackingsystem.service.AuthService;
import com.mahak.capstone.interviewprocesstrackingsystem.service.EmailService;

import java.time.LocalDate;
import java.util.UUID;
import java.util.Optional;

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
    private final CandidateRepository candidateRepository;
    private final PanelProfileRepository panelRepository;

    @Value("${app.frontend.url:http://127.0.0.1:5500/frontend/src/pages}")
    private String frontendUrl;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           PasswordTokenRepository passwordTokenRepository,
                           EmailService emailService,
                           CandidateRepository candidateRepository,
                           PanelProfileRepository panelRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.passwordTokenRepository = passwordTokenRepository;
        this.emailService = emailService;
        this.candidateRepository = candidateRepository;
        this.panelRepository = panelRepository;
    }

    /**
     * Registers a new user without password.
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

        // Set a temporary random password
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().isEmpty()) {
            user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        }
        user.setGender(dto.getGender());
        user.setMobileNumber(dto.getMobileNumber());

        String email = dto.getEmail().trim().toLowerCase();
        Role role;
        if (email.contains(".hr@")) {
            role = Role.HR;
        } else if (dto.getRole() != null && dto.getRole() == Role.HR) {
            logger.error("HR registration attempt with non-HR email: {}", dto.getEmail());
            throw new InvalidRequestException(ErrorConstants.HR_EMAIL_REQUIRED);
        } else {
            role = (dto.getRole() != null) ? dto.getRole() : Role.CANDIDATE;
        }
        user.setRole(role);

        userRepository.save(user);

        PasswordToken token = new PasswordToken(dto.getEmail().trim());
        passwordTokenRepository.save(token);

        String setupUrl = frontendUrl + "/set-password.html?token=" + token.getToken();
        emailService.sendPasswordSetupEmail(dto.getEmail().trim(), dto.getFullName().trim(), setupUrl);

        logger.info("User registered successfully: {}", dto.getEmail());
    }

    /**
     * Sets the user's password using a valid token.
     */
    @Override
    public void setPassword(SetPasswordRequestDTO dto) {
        logger.info("Password setup attempt with token: {}", dto.getToken());
        PasswordToken token = passwordTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new InvalidRequestException(ErrorConstants.INVALID_TOKEN));

        if (token.isUsed() || token.isExpired()) {
            logger.error("Password setup failed - Token expired or already used");
            throw new InvalidRequestException(ErrorConstants.TOKEN_EXPIRED_USED);
        }

        User user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        token.setUsed(true);
        passwordTokenRepository.save(token);
    }

    /**
     * Authenticates a user and generates a JWT token.
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidRequestException(ErrorConstants.INVALID_CREDENTIALS);
        }

        String role = user.getRole().name();
        String token = jwtUtil.generateToken(user.getEmail(), role);

        Long profileId = null;
        if (user.getRole() == Role.PANEL) {
            profileId = panelRepository.findByUserEmail(user.getEmail())
                    .map(com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile::getId).orElse(null);
        } else if (user.getRole() == Role.CANDIDATE) {
            profileId = candidateRepository.findByUserEmail(user.getEmail())
                    .map(com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile::getId).orElse(null);
        }

        return new LoginResponseDTO(token, role, user.getId(), profileId, user.getFullName());
    }

    @Override
    public User getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND));
    }

}