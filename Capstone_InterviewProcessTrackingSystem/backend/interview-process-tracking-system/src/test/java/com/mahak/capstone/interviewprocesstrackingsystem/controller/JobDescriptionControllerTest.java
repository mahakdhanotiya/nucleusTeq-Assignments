package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.JobDescriptionService;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;

@WebMvcTest(JobDescriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class JobDescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobDescriptionService jobService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateJob_Success() throws Exception {
        JobRequestDTO dto = new JobRequestDTO();
        dto.setTitle("Software Engineer");
        dto.setDescription("Test Desc");
        dto.setSkills(java.util.List.of("Java"));
        dto.setMinExperience(1);
        dto.setMaxExperience(5);
        dto.setLocation("Remote");
        dto.setJobType(com.mahak.capstone.interviewprocesstrackingsystem.enums.JobType.FULL_TIME);

        JobResponseDTO response = new JobResponseDTO();
        response.setId(1L);
        response.setTitle("Software Engineer");

        when(jobService.createJob(any(JobRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testGetAllJobs_Success() throws Exception {
        JobResponseDTO job = new JobResponseDTO();
        job.setId(1L);
        job.setTitle("DevOps");

        when(jobService.getAllActiveJobs()).thenReturn(List.of(job));

        mockMvc.perform(get("/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("DevOps"));
    }

    @Test
    void testDeactivateJob_Success() throws Exception {
        doNothing().when(jobService).deactivateJob(anyLong());

        mockMvc.perform(put("/jobs/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testUpdateJob_Success() throws Exception {
        JobRequestDTO dto = new JobRequestDTO();
        dto.setTitle("Senior Dev");

        JobResponseDTO response = new JobResponseDTO();
        response.setId(1L);
        response.setTitle("Senior Dev");

        when(jobService.updateJob(anyLong(), any(JobRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/jobs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Senior Dev"));
    }
}
