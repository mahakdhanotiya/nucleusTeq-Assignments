package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import org.springframework.stereotype.Component;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;

@Component
public class PanelProfileMapper {

    /**
     * Convert RequestDTO → Entity
     */
    public PanelProfile toEntity(PanelProfileRequestDTO dto, User user) {

        PanelProfile panel = new PanelProfile();

        panel.setOrganization(dto.getOrganization());
        panel.setDesignation(dto.getDesignation());
        panel.setMobileNumber(dto.getMobileNumber());
        panel.setUser(user);

        return panel;
    }

    /**
     * Convert Entity → ResponseDTO
     */
    public PanelProfileResponseDTO toResponseDTO(PanelProfile panel) {

        PanelProfileResponseDTO dto = new PanelProfileResponseDTO();

        dto.setId(panel.getId());
        dto.setOrganization(panel.getOrganization());
        dto.setDesignation(panel.getDesignation());
        dto.setMobileNumber(panel.getMobileNumber());

        // user data
        if (panel.getUser() != null) {
            dto.setName(
                panel.getUser().getFullName() != null 
                    ? panel.getUser().getFullName() 
                    : "N/A"
            );

            dto.setEmail(
                panel.getUser().getEmail() != null 
                    ? panel.getUser().getEmail() 
                    : "N/A"
            );

        }
        return dto;
    }
}
        