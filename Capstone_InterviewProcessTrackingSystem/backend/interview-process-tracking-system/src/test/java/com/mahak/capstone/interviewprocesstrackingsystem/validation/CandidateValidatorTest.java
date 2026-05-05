package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationSource;

public class CandidateValidatorTest {

    @Test
    void testValidateCreateCandidate_Success() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setTotalExperience(5);
        dto.setRelevantExperience(3);
        dto.setMobileNumber("9876543210");
        dto.setResumeUrl("http://example.com/resume.pdf");
        dto.setSource(ApplicationSource.LINKEDIN); // Fixed: Use Enum instead of String
        
        assertDoesNotThrow(() -> CandidateValidator.validateCreateCandidate(dto));
    }

    @Test
    void testValidateCreateCandidate_NegativeExperience() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setTotalExperience(-1);
        
        assertThrows(InvalidRequestException.class, () -> CandidateValidator.validateCreateCandidate(dto));
    }

    @Test
    void testValidateCreateCandidate_InvalidMobile() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setTotalExperience(5);
        dto.setMobileNumber("123");
        
        assertThrows(InvalidRequestException.class, () -> CandidateValidator.validateCreateCandidate(dto));
    }

    @Test
    void testValidateCreateCandidate_InvalidResumeFormat() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setTotalExperience(5);
        dto.setResumeUrl("http://example.com/resume.docx");
        
        assertThrows(InvalidRequestException.class, () -> CandidateValidator.validateCreateCandidate(dto));
    }

    @Test
    void testValidateCreateCandidate_NullDto() {
        assertThrows(InvalidRequestException.class, () -> CandidateValidator.validateCreateCandidate(null));
    }

    @Test
    void testValidateCreateCandidate_NegativeRelevantExperience() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setTotalExperience(5);
        dto.setRelevantExperience(-1);
        assertThrows(InvalidRequestException.class, () -> CandidateValidator.validateCreateCandidate(dto));
    }

    @Test
    void testValidateCreateCandidate_RelevantExceedsTotal() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setTotalExperience(5);
        dto.setRelevantExperience(10);
        assertThrows(InvalidRequestException.class, () -> CandidateValidator.validateCreateCandidate(dto));
    }

    @Test
    void testValidateCreateCandidate_NegativeCTC() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setTotalExperience(5);
        dto.setCurrentCTC(-1000.0);
        assertThrows(InvalidRequestException.class, () -> CandidateValidator.validateCreateCandidate(dto));
    }

    @Test
    void testValidateCreateCandidate_MissingResume() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setTotalExperience(5);
        dto.setResumeUrl("");
        assertThrows(InvalidRequestException.class, () -> CandidateValidator.validateCreateCandidate(dto));
    }
}
