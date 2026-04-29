package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;

/**
 * Validation class for Feedback module.
 */
@Component
public class FeedbackValidation {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackValidation.class);

    /**
     * Validate feedback request data.
     *
     * @param dto FeedbackRequestDTO
     */
    public void validateFeedbackRequest(FeedbackRequestDTO dto) {

        logger.debug("Validating feedback request");

        if (Objects.isNull(dto.getInterviewId())) {
            logger.error("Interview ID is null");
            throw new InvalidRequestException(ErrorConstants.INVALID_INTERVIEW_ID);
        }



        if (Objects.isNull(dto.getRating()) || dto.getRating() < 1 || dto.getRating() > 5) {
            logger.error("Invalid rating: {}", dto.getRating());
            throw new InvalidRequestException(ErrorConstants.INVALID_RATING);
        }

        if (Objects.isNull(dto.getComments()) || dto.getComments().isBlank()) {
            logger.error("Comments are invalid");
            throw new InvalidRequestException(ErrorConstants.INVALID_COMMENTS);
        }
    }
}