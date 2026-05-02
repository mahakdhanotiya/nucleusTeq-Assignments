package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewUpdateDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.StageProgressionRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.InterviewService;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;

@WebMvcTest(InterviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "HR")
public class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterviewService interviewService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testScheduleInterview_Success() throws Exception {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setCandidateId(1L);
        dto.setJobDescriptionId(1L);
        dto.setInterviewDateTime(java.time.LocalDateTime.now().plusDays(1));
        dto.setStage("L1");
        dto.setFocusArea("Technical");

        InterviewResponseDTO response = new InterviewResponseDTO();
        response.setId(1L);

        when(interviewService.scheduleInterview(any(InterviewRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/interviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testAssignPanel_Success() throws Exception {
        PanelAssignmentRequestDTO dto = new PanelAssignmentRequestDTO();
        dto.setInterviewId(1L);
        dto.setPanelId(1L);

        doNothing().when(interviewService).assignPanel(any(PanelAssignmentRequestDTO.class));

        mockMvc.perform(post("/api/interviews/assign-panel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetAllInterviews_Success() throws Exception {
        InterviewResponseDTO response = new InterviewResponseDTO();
        response.setId(1L);

        when(interviewService.getAllInterviews()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/interviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void testProgressStage_Success() throws Exception {
        StageProgressionRequestDTO dto = new StageProgressionRequestDTO();
        dto.setCandidateId(1L);
        dto.setNewStage("L1");

        when(interviewService.progressCandidateStage(any(StageProgressionRequestDTO.class))).thenReturn(new CandidateResponseDTO());

        mockMvc.perform(post("/api/interviews/stage-progression")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testUpdateStatus_Success() throws Exception {
        doNothing().when(interviewService).updateInterviewStatus(anyLong(), any());

        mockMvc.perform(put("/api/interviews/1/status")
                .param("status", "CANCELLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testUpdateInterview_Success() throws Exception {
        InterviewUpdateDTO dto = new InterviewUpdateDTO();
        dto.setFocusArea("Technical Round");
        dto.setInterviewDateTime(java.time.LocalDateTime.now().plusDays(1));

        InterviewResponseDTO response = new InterviewResponseDTO();
        response.setId(1L);

        when(interviewService.updateInterview(anyLong(), any(InterviewUpdateDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/interviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeleteInterview_Success() throws Exception {
        doNothing().when(interviewService).deleteInterview(anyLong());

        mockMvc.perform(delete("/api/interviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetInterviewById_Success() throws Exception {
        when(interviewService.getInterviewById(1L)).thenReturn(new InterviewResponseDTO());

        mockMvc.perform(get("/api/interviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetInterviewsByCandidate_Success() throws Exception {
        when(interviewService.getInterviewsByCandidate(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/interviews/candidate/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
