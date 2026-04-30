package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.JobDescriptionRepository;

@ExtendWith(MockitoExtension.class)
class JobDescriptionServiceImplTest {

    @Mock private JobDescriptionRepository jdRepository;

    @InjectMocks private JobDescriptionServiceImpl jobService;

    @Test
    void createJob_Success() {
        JobRequestDTO dto = new JobRequestDTO();
        dto.setTitle("Backend Engineer");
        dto.setDescription("Java developer role");
        dto.setLocation("Pune");
        dto.setSkills(Collections.singletonList("Java"));
        dto.setMinExperience(2);
        dto.setMaxExperience(5);

        when(jdRepository.save(any())).thenAnswer(i -> {
            JobDescription jd = (JobDescription) i.getArguments()[0];
            return jd;
        });

        JobResponseDTO response = jobService.createJob(dto);
        assertNotNull(response);
        assertEquals("Backend Engineer", response.getTitle());
    }

    @Test
    void deactivateJob_Success() {
        JobDescription jd = new JobDescription();
        jd.setIsActive(true);
        when(jdRepository.findById(1L)).thenReturn(Optional.of(jd));
        
        jobService.deactivateJob(1L);
        
        assertFalse(jd.getIsActive());
        verify(jdRepository, times(1)).save(jd);
    }

    @Test
    void deactivateJob_Fail_AlreadyInactive() {
        JobDescription jd = new JobDescription();
        jd.setIsActive(false);
        when(jdRepository.findById(1L)).thenReturn(Optional.of(jd));
        
        assertThrows(InvalidRequestException.class, () -> jobService.deactivateJob(1L));
    }

    @Test
    void activateJob_Success() {
        JobDescription jd = new JobDescription();
        jd.setIsActive(false);
        when(jdRepository.findById(1L)).thenReturn(Optional.of(jd));
        
        jobService.activateJob(1L);
        
        assertTrue(jd.getIsActive());
        verify(jdRepository, times(1)).save(jd);
    }

    @Test
    void activateJob_Fail_NotFound() {
        when(jdRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> jobService.activateJob(1L));
    }

    @Test
    void updateJob_Success() {
        JobDescription existing = new JobDescription();
        existing.setId(1L);
        existing.setTitle("Old Title");
        
        JobRequestDTO dto = new JobRequestDTO();
        dto.setTitle("New Title");
        dto.setDescription("New Desc");
        dto.setLocation("New Loc");
        
        when(jdRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(jdRepository.save(any())).thenReturn(existing);

        JobResponseDTO response = jobService.updateJob(1L, dto);
        
        assertEquals("New Title", response.getTitle());
        verify(jdRepository, times(1)).save(existing);
    }
}
