package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;

public class InterviewMapperTest {

    private final InterviewMapper mapper = new InterviewMapper();

    @Test
    void testToEntity() {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setInterviewDateTime(LocalDateTime.now());
        dto.setStage("L1");
        
        CandidateProfile candidate = new CandidateProfile();
        org.springframework.test.util.ReflectionTestUtils.setField(candidate, "id", 1L);
        
        JobDescription job = new JobDescription();
        org.springframework.test.util.ReflectionTestUtils.setField(job, "id", 2L);
        
        Interview entity = mapper.toEntity(dto, candidate, job);
        
        assertNotNull(entity);
        assertEquals(InterviewStage.L1, entity.getStage());
        assertEquals(candidate, entity.getCandidate());
    }

    @Test
    void testToDTO() {
        Interview entity = new Interview();
        org.springframework.test.util.ReflectionTestUtils.setField(entity, "id", 1L);
        entity.setStage(InterviewStage.L1);
        entity.setStatus(com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus.SCHEDULED);
        
        CandidateProfile candidate = new CandidateProfile();
        org.springframework.test.util.ReflectionTestUtils.setField(candidate, "id", 1L);
        User user = new User();
        user.setFullName("Jane");
        candidate.setUser(user);
        entity.setCandidate(candidate);
        
        JobDescription job = new JobDescription();
        org.springframework.test.util.ReflectionTestUtils.setField(job, "id", 2L);
        job.setTitle("SDE");
        entity.setJobDescription(job);
        
        InterviewResponseDTO dto = mapper.toResponseDTO(entity);
        
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Jane", dto.getCandidateName());
    }
}
