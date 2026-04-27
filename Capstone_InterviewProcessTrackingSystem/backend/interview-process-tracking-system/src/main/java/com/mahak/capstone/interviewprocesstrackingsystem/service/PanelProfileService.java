package com.mahak.capstone.interviewprocesstrackingsystem.service;

import java.util.List;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileResponseDTO;

/**
 * Service interface for managing panel profiles
 */
public interface PanelProfileService {

    /**
     * Create a new panel profile.
     *
     * @param dto PanelProfileRequestDTO containing panel details
     * @return PanelProfileResponseDTO
     */
    PanelProfileResponseDTO createPanel(PanelProfileRequestDTO dto);

    /**
     * Fetch panel profile by ID.
     *
     * @param id panel id
     * @return PanelProfileResponseDTO
     */
    PanelProfileResponseDTO getPanelById(Long id);

    /**
     * Fetch all panel profiles.
     * Used by HR for panel selection dropdown.
     *
     * @return list of PanelProfileResponseDTO
     */
    List<PanelProfileResponseDTO> getAllPanels();

    /**
     * Update an existing panel profile.
     *
     * @param id panel id
     * @param dto PanelProfileRequestDTO with updated details
     * @return PanelProfileResponseDTO
     */
    PanelProfileResponseDTO updatePanel(Long id, PanelProfileRequestDTO dto);

    /**
     * HR: Delete panel profile by ID.
     */
    void deletePanel(Long id);
}