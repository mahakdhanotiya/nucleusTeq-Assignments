package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;

public class CandidateMapperTest {

    @Test
    void testToEntity() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setMobileNumber("1234567890");
        dto.setResumeUrl("http://resume.com");
        dto.setTotalExperience(5);
        
        User user = new User();
        user.setId(1L);
        
        JobDescription job = new JobDescription();
        job.setId(2L);
        
        CandidateProfile entity = CandidateMapper.toEntity(dto, user, job);
        
        assertNotNull(entity);
        assertEquals("1234567890", entity.getMobileNumber());
        assertEquals(user, entity.getUser());
        assertEquals(job, entity.getJobDescription());
    }

    @Test
    void testToDTO() {
        CandidateProfile c = new CandidateProfile();
        org.springframework.test.util.ReflectionTestUtils.setField(c, "id", 1L);
        c.setMobileNumber("1234567890");
        c.setCurrentStage(InterviewStage.L1);
        c.setApplicationStatus(ApplicationStatus.APPLIED);
        
        User user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        c.setUser(user);
        
        JobDescription job = new JobDescription();
        job.setId(2L);
        job.setTitle("Dev");
        c.setJobDescription(job);
        
        CandidateResponseDTO dto = CandidateMapper.toDTO(c);
        
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getFullName());
        assertEquals("Dev", dto.getJobTitle());
    }
}
