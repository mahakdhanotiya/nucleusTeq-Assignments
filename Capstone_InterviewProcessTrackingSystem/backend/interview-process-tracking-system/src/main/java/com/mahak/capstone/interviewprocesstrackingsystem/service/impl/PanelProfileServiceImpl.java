package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PasswordToken;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PasswordTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.PanelProfileMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PanelProfileRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.service.EmailService;
import com.mahak.capstone.interviewprocesstrackingsystem.service.PanelProfileService;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.Role;
import com.mahak.capstone.interviewprocesstrackingsystem.validation.PanelProfileValidation;

/**
 * Implementation of PanelProfileService.
 * 
 * Handles panel creation and retrieval logic.
 * Includes validation, logging, and proper exception handling.
 */
@Service
public class PanelProfileServiceImpl implements PanelProfileService {

    private static final Logger logger =
            LoggerFactory.getLogger(PanelProfileServiceImpl.class);

    private final PanelProfileRepository panelRepository;
    private final UserRepository userRepository;
    private final PanelProfileMapper panelMapper;
    private final PanelProfileValidation panelValidation;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordTokenRepository passwordTokenRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public PanelProfileServiceImpl(
            PanelProfileRepository panelRepository,
            UserRepository userRepository,
            PanelProfileMapper panelMapper,
            PanelProfileValidation panelValidation,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            PasswordTokenRepository passwordTokenRepository,
            org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {

        this.panelRepository = panelRepository;
        this.userRepository = userRepository;
        this.panelMapper = panelMapper;
        this.panelValidation = panelValidation;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.passwordTokenRepository = passwordTokenRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Creates a new panel profile.
     *
     * Flow:
     * - Validate request
     * - Check if user exists
     * - Check duplicate panel
     * - Map DTO → Entity
     * - Save entity
     * - Return response DTO
     *
     * @param dto PanelProfileRequestDTO
     * @return PanelProfileResponseDTO
     */
    @Override
    public PanelProfileResponseDTO createPanel(PanelProfileRequestDTO dto) {

        logger.info("Creating panel for email: {}", dto.getEmail());

        // validation
        panelValidation.validateCreatePanel(dto);

        // Check if user already exists
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);
        if (user == null) {
            user = new User();
            user.setFullName(dto.getFullName());
            user.setEmail(dto.getEmail());
            // random password because it will be set via email
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setRole(Role.PANEL);
            user.setMobileNumber(dto.getMobileNumber());
            user = userRepository.save(user);

            // Generate password setup token and send email
            PasswordToken token = new PasswordToken(dto.getEmail());
            passwordTokenRepository.save(token);
            String setupUrl = frontendUrl + "/set-password.html?token=" + token.getToken();
            emailService.sendPanelOnboardingEmail(dto.getEmail(), dto.getFullName(), setupUrl);
        } else {
             // duplicate check
             if (panelRepository.existsByUserId(user.getId())) {
                 logger.error("Panel already exists for userId: {}", user.getId());
                 throw new InvalidRequestException(ErrorConstants.PANEL_ALREADY_EXISTS);
             }
        }

        // map DTO → Entity
        PanelProfile panel = panelMapper.toEntity(dto, user);

        // save
        panel = panelRepository.save(panel);

        logger.info("Panel created successfully with id: {}", panel.getId());

        // return response
        return panelMapper.toResponseDTO(panel);
    }

    /**
     * Fetch panel profile by ID.
     *
     * @param id panel id
     * @return PanelProfileResponseDTO
     */
    @Override
    public PanelProfileResponseDTO getPanelById(Long id) {

        logger.info("Fetching panel with id: {}", id);

        PanelProfile panel = panelRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Panel not found: {}", id);
                    return new ResourceNotFoundException(ErrorConstants.PANEL_NOT_FOUND);
                });

        return panelMapper.toResponseDTO(panel);
    }

    /**
     * Fetch all panel profiles.
     * Used by HR for panel selection dropdown.
     *
     * @return list of PanelProfileResponseDTO
     */
    @Override
    public List<PanelProfileResponseDTO> getAllPanels() {

        logger.info("Fetching all panels");

        List<PanelProfile> panels = panelRepository.findAll();

        return panels.stream()
                .map(panelMapper::toResponseDTO)
                .toList();
    }

    /**
     * Update an existing panel profile.
     *
     * @param id panel id
     * @param dto updated details
     * @return PanelProfileResponseDTO
     */
    @Override
    public PanelProfileResponseDTO updatePanel(Long id, PanelProfileRequestDTO dto) {

        logger.info("Updating panel with id: {}", id);

        PanelProfile panel = panelRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Panel not found for update: {}", id);
                    return new ResourceNotFoundException(ErrorConstants.PANEL_NOT_FOUND);
                });

        // Update editable fields
        if (dto.getOrganization() != null) {
            panel.setOrganization(dto.getOrganization());
        }
        if (dto.getDesignation() != null) {
            panel.setDesignation(dto.getDesignation());
        }
        if (dto.getMobileNumber() != null) {
            panel.setMobileNumber(dto.getMobileNumber());
        }

        if (dto.getEmail() != null && panel.getUser() != null) {
            panel.getUser().setEmail(dto.getEmail());
            userRepository.save(panel.getUser());
        }

        panel = panelRepository.save(panel);

        logger.info("Panel updated successfully: {}", id);

        return panelMapper.toResponseDTO(panel);
    }

    /**
     * HR: Delete panel profile by ID.
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deletePanel(Long id) {
        logger.info("Deleting panel with id: {}", id);
        PanelProfile panel = panelRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Panel not found for deletion: {}", id);
                    return new ResourceNotFoundException(ErrorConstants.PANEL_NOT_FOUND);
                });
                
        try {
            // Remove assignment rows and nullify foreign keys to prevent constraint violations
            jdbcTemplate.update("DELETE FROM interview_panel_assignments WHERE panel_id = ?", id);
            jdbcTemplate.update("UPDATE feedbacks SET panel_id = NULL WHERE panel_id = ?", id);
        } catch (Exception e) {
            logger.warn("Could not handle foreign keys for panel {}: {}", id, e.getMessage());
        }

        panelRepository.delete(panel);

        if (panel.getUser() != null) {
            try {
                userRepository.delete(panel.getUser());
            } catch(Exception e) {
                 logger.warn("Could not delete user for panel {}: {}", id, e.getMessage());
            }
        }

        logger.info("Panel deleted successfully: {}", id);
    }
}