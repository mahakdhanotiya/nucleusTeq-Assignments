package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.CandidateRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.JobDescriptionRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.security.CurrentUserUtil;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {

    @Mock private CandidateRepository candidateRepository;
    @Mock private UserRepository userRepository;
    @Mock private JobDescriptionRepository jobRepository;

    @InjectMocks private CandidateServiceImpl candidateService;

    private MockedStatic<CurrentUserUtil> mockedCurrentUserUtil;
    private User user;
    private CandidateProfile candidate;
    private JobDescription jd;

    @BeforeEach
    void setUp() {
        mockedCurrentUserUtil = mockStatic(CurrentUserUtil.class);
        
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("test@candidate.com");
        
        jd = new JobDescription();
        ReflectionTestUtils.setField(jd, "id", 1L);
        jd.setTitle("Software Engineer");

        candidate = new CandidateProfile();
        ReflectionTestUtils.setField(candidate, "id", 1L);
        candidate.setUser(user);
        candidate.setJobDescription(jd);
        candidate.setResumeUrl("resume.pdf");
        candidate.setCurrentStage(InterviewStage.PROFILING);
        candidate.setApplicationStatus(ApplicationStatus.PROFILING_COMPLETED);
        candidate.setTotalExperience(5);
        candidate.setMobileNumber("1234567890");
    }

    @AfterEach
    void tearDown() {
        mockedCurrentUserUtil.close();
    }

    @Test
    void createCandidate_Fail_ActiveApplication() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setUserId(1L);
        dto.setTotalExperience(5);
        dto.setMobileNumber("1234567890");
        dto.setResumeUrl("resume.pdf");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(candidateRepository.existsByUserAndApplicationStatusNot(user, ApplicationStatus.REJECTED)).thenReturn(true);
        
        assertThrows(InvalidRequestException.class, () -> candidateService.createCandidate(dto));
    }

    @Test
    void createCandidate_Fail_InvalidResume() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setUserId(1L);
        dto.setJobId(1L);
        dto.setResumeUrl("file.txt"); // Not PDF
        dto.setTotalExperience(5);
        dto.setMobileNumber("1234567890");
        
        // No stubbing needed here because Validator fails early
        assertThrows(InvalidRequestException.class, () -> candidateService.createCandidate(dto));
    }

    @Test
    void getMyProfile_Success() {
        mockedCurrentUserUtil.when(CurrentUserUtil::getCurrentUserEmail).thenReturn("test@candidate.com");
        when(userRepository.findByEmail("test@candidate.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUser(user)).thenReturn(Optional.of(candidate));
        
        CandidateResponseDTO result = candidateService.getMyProfile();
        assertNotNull(result);
    }

    @Test
    void updateMyProfile_Success() {
        mockedCurrentUserUtil.when(CurrentUserUtil::getCurrentUserEmail).thenReturn("test@candidate.com");
        when(userRepository.findByEmail("test@candidate.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUser(user)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any())).thenReturn(candidate);
        
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setMobileNumber("9999999999");
        
        CandidateResponseDTO result = candidateService.updateMyProfile(dto);
        assertEquals("9999999999", candidate.getMobileNumber());
    }

    @Test
    void searchCandidates_Success() {
        when(candidateRepository.findByFilters(any(), any(), any())).thenReturn(List.of(candidate));
        List<CandidateResponseDTO> list = candidateService.searchCandidates(1L, InterviewStage.L1, ApplicationStatus.EVALUATED);
        assertFalse(list.isEmpty());
    }

    @Test
    void getCandidateById_NotFound_Exception() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> candidateService.getCandidateById(1L));
    }

    @Test
    void getMyProfile_UserNotFound_Exception() {
        mockedCurrentUserUtil.when(CurrentUserUtil::getCurrentUserEmail).thenReturn("unknown@test.com");
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> candidateService.getMyProfile());
    }

    @Test
    void getMyProfile_CandidateNotFound_Exception() {
        mockedCurrentUserUtil.when(CurrentUserUtil::getCurrentUserEmail).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUser(user)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> candidateService.getMyProfile());
    }

    @Test
    void updateMyProfile_UserNotFound_Exception() {
        mockedCurrentUserUtil.when(CurrentUserUtil::getCurrentUserEmail).thenReturn("unknown@test.com");
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> candidateService.updateMyProfile(new CandidateRequestDTO()));
    }

    @Test
    void updateMyProfile_CandidateNotFound_Exception() {
        mockedCurrentUserUtil.when(CurrentUserUtil::getCurrentUserEmail).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUser(user)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> candidateService.updateMyProfile(new CandidateRequestDTO()));
    }

    @Test
    void createCandidate_JobNotFound_Exception() {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setUserId(1L);
        dto.setJobId(999L);
        dto.setTotalExperience(5);
        dto.setMobileNumber("1234567890");
        dto.setResumeUrl("resume.pdf");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(candidateRepository.existsByUserAndApplicationStatusNot(user, ApplicationStatus.REJECTED)).thenReturn(false);
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> candidateService.createCandidate(dto));
    }
}
