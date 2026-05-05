package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;

public class FeedbackValidationTest {

    private final FeedbackValidation validation = new FeedbackValidation();

    @Test
    void testValidateFeedbackRequest_Success() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setInterviewId(1L);
        dto.setRating(5);
        dto.setComments("Excellent");
        
        assertDoesNotThrow(() -> validation.validateFeedbackRequest(dto));
    }

    @Test
    void testValidateFeedbackRequest_InvalidRating() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setInterviewId(1L);
        dto.setRating(10);
        
        assertThrows(InvalidRequestException.class, () -> validation.validateFeedbackRequest(dto));
    }

    @Test
    void testValidateFeedbackRequest_EmptyComments() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setInterviewId(1L);
        dto.setRating(3);
        dto.setComments("");
        
        assertThrows(InvalidRequestException.class, () -> validation.validateFeedbackRequest(dto));
    }

    @Test
    void testValidateFeedbackRequest_NullInterviewId() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setRating(3);
        dto.setComments("Good");
        assertThrows(InvalidRequestException.class, () -> validation.validateFeedbackRequest(dto));
    }
}
