package com.mahak.capstone.interviewprocesstrackingsystem.service;

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
}