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
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.PanelProfileMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PanelProfileRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.service.EmailService;
import com.mahak.capstone.interviewprocesstrackingsystem.service.PanelProfileService;
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

    public PanelProfileServiceImpl(
            PanelProfileRepository panelRepository,
            UserRepository userRepository,
            PanelProfileMapper panelMapper,
            PanelProfileValidation panelValidation,
            EmailService emailService) {

        this.panelRepository = panelRepository;
        this.userRepository = userRepository;
        this.panelMapper = panelMapper;
        this.panelValidation = panelValidation;
        this.emailService = emailService;
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

        logger.info("Creating panel for userId: {}", dto.getUserId());

        // validation
        panelValidation.validateCreatePanel(dto);

        // fetch user
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    logger.error("User not found: {}", dto.getUserId());
                    return new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND);
                });

        // duplicate check
        if (panelRepository.existsByUserId(dto.getUserId())) {
            logger.error("Panel already exists for userId: {}", dto.getUserId());
            throw new InvalidRequestException(ErrorConstants.PANEL_ALREADY_EXISTS);
        }

        // map DTO → Entity
        PanelProfile panel = panelMapper.toEntity(dto, user);

        // save
        panel = panelRepository.save(panel);

        logger.info("Panel created successfully with id: {}", panel.getId());

        // Send onboarding email to panel member
        try {
            emailService.sendPanelOnboardingEmail(
                    user.getEmail(),
                    user.getFullName(),
                    "http://localhost:5500/pages/login.html");
            logger.info("Onboarding email sent to panel: {}", user.getEmail());
        } catch (Exception e) {
            logger.warn("Email send failed (non-blocking): {}", e.getMessage());
        }

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

        panel = panelRepository.save(panel);

        logger.info("Panel updated successfully: {}", id);

        return panelMapper.toResponseDTO(panel);
    }

    /**
     * HR: Delete panel profile by ID.
     */
    @Override
    public void deletePanel(Long id) {
        logger.info("Deleting panel with id: {}", id);
        PanelProfile panel = panelRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Panel not found for deletion: {}", id);
                    return new ResourceNotFoundException(ErrorConstants.PANEL_NOT_FOUND);
                });
        panelRepository.delete(panel);
        logger.info("Panel deleted successfully: {}", id);
    }
}