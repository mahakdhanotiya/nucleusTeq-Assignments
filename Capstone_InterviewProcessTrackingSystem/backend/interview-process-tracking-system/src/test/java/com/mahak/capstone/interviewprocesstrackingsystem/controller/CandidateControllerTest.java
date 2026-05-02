package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.CandidateService;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;

@WebMvcTest(CandidateController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CandidateService candidateService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCandidate_Success() throws Exception {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setUserId(1L);
        dto.setJobId(1L);
        dto.setMobileNumber("1234567890");
        dto.setResumeUrl("http://example.com/resume.pdf");
        dto.setTotalExperience(5);
        dto.setSource(com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationSource.LINKEDIN);

        CandidateResponseDTO response = new CandidateResponseDTO();
        response.setId(1L);
        response.setFullName("John Doe");

        when(candidateService.createCandidate(any(CandidateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("John Doe"));
    }

    @Test
    void testGetAllCandidates_Success() throws Exception {
        CandidateResponseDTO candidate = new CandidateResponseDTO();
        candidate.setId(1L);
        candidate.setFullName("Alice");

        when(candidateService.getAllCandidates()).thenReturn(List.of(candidate));

        mockMvc.perform(get("/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].fullName").value("Alice"));
    }

    @Test
    void testGetMyProfile_Success() throws Exception {
        CandidateResponseDTO response = new CandidateResponseDTO();
        response.setId(1L);
        response.setFullName("John Doe");

        when(candidateService.getMyProfile()).thenReturn(response);

        mockMvc.perform(get("/candidates/my-profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("John Doe"));
    }

    @Test
    void testUpdateMyProfile_Success() throws Exception {
        CandidateRequestDTO dto = new CandidateRequestDTO();
        dto.setMobileNumber("1234567890");

        CandidateResponseDTO response = new CandidateResponseDTO();
        response.setId(1L);
        response.setMobileNumber("1234567890");

        when(candidateService.updateMyProfile(any(CandidateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/candidates/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mobileNumber").value("1234567890"));
    }

    @Test
    void testDeleteCandidate_Success() throws Exception {
        doNothing().when(candidateService).deleteCandidate(anyLong());

        mockMvc.perform(delete("/candidates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSearchCandidates_Success() throws Exception {
        when(candidateService.searchCandidates(any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/candidates/search")
                .param("jdId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetCandidateById_Success() throws Exception {
        when(candidateService.getCandidateById(1L)).thenReturn(new CandidateResponseDTO());

        mockMvc.perform(get("/candidates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
