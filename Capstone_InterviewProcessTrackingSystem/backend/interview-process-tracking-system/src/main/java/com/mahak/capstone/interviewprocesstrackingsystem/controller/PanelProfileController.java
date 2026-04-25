package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.PanelProfileService;

/**
 * REST Controller for Panel Profile operations
 */
@RestController
@RequestMapping("/api/panels")
public class PanelProfileController {

    private static final Logger logger =
            LoggerFactory.getLogger(PanelProfileController.class);

    private final PanelProfileService panelService;

    public PanelProfileController(PanelProfileService panelService) {
        this.panelService = panelService;
    }

    /**
     * HR: Create panel
     */
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<PanelProfileResponseDTO> createPanel(
            @RequestBody PanelProfileRequestDTO dto) {

        logger.info("HR creating panel");

        PanelProfileResponseDTO response = panelService.createPanel(dto);

        return ResponseEntity.ok(response);
    }

    /**
     * HR / Panel: Get panel by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR','PANEL')")
    public ResponseEntity<PanelProfileResponseDTO> getPanelById(
            @PathVariable Long id) {

        logger.info("Fetching panel with id: {}", id);

        PanelProfileResponseDTO response = panelService.getPanelById(id);

        return ResponseEntity.ok(response);
    }
}