package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ApiConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.PanelProfileService;

/**
 * REST Controller for Panel Profile operations.
 */
@RestController
@RequestMapping(ApiConstants.PANELS)
public class PanelProfileController {

    private static final Logger logger = LoggerFactory.getLogger(PanelProfileController.class);

    private final PanelProfileService panelService;

    public PanelProfileController(PanelProfileService panelService) {
        this.panelService = panelService;
    }

    /**
     * HR: Create panel member.
     * POST /api/panels
     */
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<PanelProfileResponseDTO> createPanel(
            @RequestBody PanelProfileRequestDTO dto) {

        logger.info("Create panel request received for userId: {}", dto.getUserId());
        PanelProfileResponseDTO response = panelService.createPanel(dto);
        logger.info("Panel created successfully with id: {}", response.getId());
        return new ApiResponseDTO<>(true, ApiConstants.PANEL_CREATED, response);
    }

    /**
     * HR: Get all panels.
     * GET /api/panels
     */
    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<List<PanelProfileResponseDTO>> getAllPanels() {

        logger.info("Fetching all panel members");
        List<PanelProfileResponseDTO> panels = panelService.getAllPanels();
        logger.info("Fetched {} panel members", panels.size());
        return new ApiResponseDTO<>(true, ApiConstants.PANELS_FETCHED, panels);
    }

    /**
     * HR / Panel: Get panel by ID.
     * GET /api/panels/{id}
     */
    @GetMapping(ApiConstants.BY_ID)
    @PreAuthorize("hasAnyRole('HR','PANEL')")
    public ApiResponseDTO<PanelProfileResponseDTO> getPanelById(
            @PathVariable Long id) {

        logger.info("Fetching panel with id: {}", id);
        PanelProfileResponseDTO response = panelService.getPanelById(id);
        logger.info("Panel fetched: {}", id);
        return new ApiResponseDTO<>(true, ApiConstants.PANEL_FETCHED, response);
    }

    /**
     * HR: Update panel member details.
     * PUT /api/panels/{id}
     */
    @PutMapping(ApiConstants.BY_ID)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<PanelProfileResponseDTO> updatePanel(
            @PathVariable Long id,
            @RequestBody PanelProfileRequestDTO dto) {

        logger.info("Update panel request for id: {}", id);
        PanelProfileResponseDTO response = panelService.updatePanel(id, dto);
        logger.info("Panel updated successfully: {}", id);
        return new ApiResponseDTO<>(true, ApiConstants.PANEL_UPDATED, response);
    }

    /**
     * HR: Delete panel member by ID.
     * DELETE /api/panels/{id}
     */
    @DeleteMapping(ApiConstants.BY_ID)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<Void> deletePanel(@PathVariable Long id) {

        logger.info("Delete panel request for id: {}", id);
        panelService.deletePanel(id);
        logger.info("Panel deleted: {}", id);
        return new ApiResponseDTO<>(true, ApiConstants.PANEL_DELETED, null);
    }
}