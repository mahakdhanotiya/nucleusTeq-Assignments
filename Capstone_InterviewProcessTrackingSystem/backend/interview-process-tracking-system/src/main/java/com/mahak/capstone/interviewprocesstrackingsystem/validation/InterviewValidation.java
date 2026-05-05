package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;

/**
 * Validation class for Interview module
 */
@Component
public class InterviewValidation {

    private static final Logger logger = LoggerFactory.getLogger(InterviewValidation.class);

    /**
     * Validate interview request
     */
    public void validateInterviewRequest(InterviewRequestDTO dto, com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage candidateCurrentStage) {

        logger.debug("Validating interview request for stage: {}", dto.getStage());

        if (Objects.isNull(dto.getInterviewDateTime())) {
            logger.error("Interview date is null");
            throw new InvalidRequestException(ErrorConstants.INVALID_INTERVIEW_DATE);
        }

        if (Objects.isNull(dto.getStage()) || dto.getStage().isBlank()) {
            logger.error("Interview stage is invalid");
            throw new InvalidRequestException(ErrorConstants.INVALID_STAGE);
        }

        if (Objects.isNull(dto.getFocusArea()) || dto.getFocusArea().isBlank()) {
            logger.error("Focus area is invalid");
            throw new InvalidRequestException(ErrorConstants.INVALID_FOCUS_AREA);
        }

        // Enforce that scheduled round matches the candidate's current stage
        try {
            com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage requestedStage = 
                com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.valueOf(dto.getStage().toUpperCase());
            
            if (requestedStage != candidateCurrentStage) {
                throw new InvalidRequestException("Cannot schedule " + requestedStage + " interview because candidate is currently in " + candidateCurrentStage + " stage. Please progress the stage first.");
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid stage name: " + dto.getStage());
        }
    }

    /**
     * Validate panel assignment request
     */
    public void validatePanelAssignment(PanelAssignmentRequestDTO dto) {

        logger.debug("Validating panel assignment request");

        if (Objects.isNull(dto.getInterviewId())) {
            logger.error("Interview ID is null");
            throw new InvalidRequestException(ErrorConstants.INVALID_INTERVIEW_ID);
        }

        if (Objects.isNull(dto.getPanelId())) {
            logger.error("Panel ID is null");
            throw new InvalidRequestException(ErrorConstants.INVALID_PANEL_ID);
        }

        if (Objects.isNull(dto.getFocusArea()) || dto.getFocusArea().isBlank()) {
            logger.error("Focus area is invalid");
            throw new InvalidRequestException(ErrorConstants.INVALID_FOCUS_AREA);
        }
    }
}