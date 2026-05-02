package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackDetailResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.FeedbackService;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;

@WebMvcTest(FeedbackController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "HR")
public class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackService feedbackService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "PANEL")
    void testSubmitFeedback_Success() throws Exception {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setInterviewId(1L);
        dto.setRating(5);
        dto.setComments("Great performance");
        dto.setStrengths("Java skills");
        dto.setWeaknesses("None");
        dto.setAreasCovered("Core Java");
        dto.setStatus("SELECTED");

        FeedbackResponseDTO response = new FeedbackResponseDTO();
        response.setId(1L);

        when(feedbackService.submitFeedback(any(FeedbackRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "HR")
    void testGetFeedbackByInterview_Success() throws Exception {
        FeedbackDetailResponseDTO detail = new FeedbackDetailResponseDTO();
        detail.setId(1L);

        when(feedbackService.getFeedbackByInterview(anyLong(), anyString(), any())).thenReturn(List.of(detail));

        mockMvc.perform(get("/api/feedback/interview/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "HR")
    void testGetFeedbackByCandidate_Success() throws Exception {
        FeedbackDetailResponseDTO detail = new FeedbackDetailResponseDTO();
        detail.setId(1L);

        when(feedbackService.getFeedbackByCandidate(anyLong())).thenReturn(List.of(detail));

        mockMvc.perform(get("/api/feedback/candidate/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
