package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;

public class InterviewValidationTest {

    private final InterviewValidation validation = new InterviewValidation();

    @Test
    void testValidateInterviewRequest_Success() {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setInterviewDateTime(LocalDateTime.now().plusDays(1));
        dto.setStage("L1");
        dto.setFocusArea("Java");
        
        assertDoesNotThrow(() -> validation.validateInterviewRequest(dto, InterviewStage.L1));
    }

    @Test
    void testValidateInterviewRequest_StageMismatch() {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setInterviewDateTime(LocalDateTime.now().plusDays(1));
        dto.setStage("L2");
        dto.setFocusArea("Java");
        
        assertThrows(InvalidRequestException.class, () -> validation.validateInterviewRequest(dto, InterviewStage.L1));
    }

    @Test
    void testValidatePanelAssignment_Success() {
        PanelAssignmentRequestDTO dto = new PanelAssignmentRequestDTO();
        dto.setInterviewId(1L);
        dto.setPanelId(1L);
        dto.setFocusArea("Java");
        
        assertDoesNotThrow(() -> validation.validatePanelAssignment(dto));
    }

    @Test
    void testValidatePanelAssignment_InvalidId() {
        PanelAssignmentRequestDTO dto = new PanelAssignmentRequestDTO();
        dto.setPanelId(1L);
        dto.setFocusArea("Java");
        
        assertThrows(InvalidRequestException.class, () -> validation.validatePanelAssignment(dto));
    }

    @Test
    void testValidateInterviewRequest_NullDate() {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setStage("L1");
        assertThrows(InvalidRequestException.class, () -> validation.validateInterviewRequest(dto, InterviewStage.L1));
    }

    @Test
    void testValidateInterviewRequest_BlankStage() {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setInterviewDateTime(LocalDateTime.now().plusDays(1));
        dto.setStage("");
        assertThrows(InvalidRequestException.class, () -> validation.validateInterviewRequest(dto, InterviewStage.L1));
    }

    @Test
    void testValidateInterviewRequest_BlankFocusArea() {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setInterviewDateTime(LocalDateTime.now().plusDays(1));
        dto.setStage("L1");
        dto.setFocusArea("");
        assertThrows(InvalidRequestException.class, () -> validation.validateInterviewRequest(dto, InterviewStage.L1));
    }

    @Test
    void testValidateInterviewRequest_InvalidStageName() {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setInterviewDateTime(LocalDateTime.now().plusDays(1));
        dto.setStage("INVALID_STAGE");
        dto.setFocusArea("Java");
        assertThrows(InvalidRequestException.class, () -> validation.validateInterviewRequest(dto, InterviewStage.L1));
    }

    @Test
    void testValidatePanelAssignment_BlankFocusArea() {
        PanelAssignmentRequestDTO dto = new PanelAssignmentRequestDTO();
        dto.setInterviewId(1L);
        dto.setPanelId(1L);
        dto.setFocusArea("");
        assertThrows(InvalidRequestException.class, () -> validation.validatePanelAssignment(dto));
    }
}
