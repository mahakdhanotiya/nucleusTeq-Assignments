package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;

public class JobValidatorTest {

    @Test
    void testValidateCreateJob_Success() {
        JobRequestDTO dto = new JobRequestDTO();
        dto.setMinExperience(2);
        dto.setMaxExperience(5);
        dto.setMinSalary(1000.0);
        dto.setMaxSalary(5000.0);
        
        assertDoesNotThrow(() -> JobValidator.validateCreateJob(dto));
    }

    @Test
    void testValidateCreateJob_InvalidExperience() {
        JobRequestDTO dto = new JobRequestDTO();
        dto.setMinExperience(5);
        dto.setMaxExperience(2);
        
        assertThrows(InvalidRequestException.class, () -> JobValidator.validateCreateJob(dto));
    }

    @Test
    void testValidateCreateJob_InvalidSalary() {
        JobRequestDTO dto = new JobRequestDTO();
        dto.setMinSalary(5000.0);
        dto.setMaxSalary(1000.0);
        
        assertThrows(InvalidRequestException.class, () -> JobValidator.validateCreateJob(dto));
    }
}
