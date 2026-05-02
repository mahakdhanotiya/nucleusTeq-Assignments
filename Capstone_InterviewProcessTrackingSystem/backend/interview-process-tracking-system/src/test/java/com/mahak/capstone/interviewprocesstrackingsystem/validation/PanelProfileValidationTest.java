package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;

public class PanelProfileValidationTest {

    private final PanelProfileValidation validation = new PanelProfileValidation();

    @Test
    void testValidateCreatePanel_Success() {
        PanelProfileRequestDTO dto = new PanelProfileRequestDTO();
        dto.setFullName("John Doe");
        dto.setEmail("john@example.com");
        dto.setOrganization("NT");
        dto.setDesignation("Lead");
        dto.setMobileNumber("1234567890");
        
        assertDoesNotThrow(() -> validation.validateCreatePanel(dto));
    }

    @Test
    void testValidateCreatePanel_MissingEmail() {
        PanelProfileRequestDTO dto = new PanelProfileRequestDTO();
        dto.setFullName("John Doe");
        dto.setOrganization("NT");
        
        assertThrows(InvalidRequestException.class, () -> validation.validateCreatePanel(dto));
    }
}
