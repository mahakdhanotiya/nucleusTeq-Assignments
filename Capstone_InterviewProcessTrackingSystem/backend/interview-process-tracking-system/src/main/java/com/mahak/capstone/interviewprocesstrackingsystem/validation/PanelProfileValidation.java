package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;

/**
 * Validation class for Panel Profile module
 */
@Component
public class PanelProfileValidation {

    private static final Logger logger = LoggerFactory.getLogger(PanelProfileValidation.class);

    /**
     * Validate panel creation request
     */
    public void validateCreatePanel(PanelProfileRequestDTO dto) {

        logger.debug("Validating panel profile request");

        if (Objects.isNull(dto.getUserId())) {
            logger.error("User ID is null");
            throw new InvalidRequestException(ErrorConstants.USER_ID_REQUIRED);
        }

        if (Objects.isNull(dto.getOrganization()) || dto.getOrganization().isBlank()) {
            logger.error("Organization is invalid");
            throw new InvalidRequestException(ErrorConstants.ORGANIZATION_REQUIRED);
        }

        if (Objects.isNull(dto.getDesignation()) || dto.getDesignation().isBlank()) {
            logger.error("Designation is invalid");
            throw new InvalidRequestException(ErrorConstants.DESIGNATION_REQUIRED);
        }

        if (Objects.isNull(dto.getMobileNumber()) || dto.getMobileNumber().isBlank()) {
            logger.error("Mobile number is invalid");
            throw new InvalidRequestException(ErrorConstants.MOBILE_REQUIRED);
        }
    }
}