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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.PanelProfileService;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;

@WebMvcTest(PanelProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "HR")
public class PanelProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PanelProfileService panelService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreatePanel_Success() throws Exception {
        PanelProfileRequestDTO dto = new PanelProfileRequestDTO();
        dto.setUserId(1L);
        dto.setOrganization("NucleusTeq");
        dto.setDesignation("Software Engineer");
        dto.setMobileNumber("9876543210");
        dto.setEmail("panel@example.com");

        PanelProfileResponseDTO response = new PanelProfileResponseDTO();
        response.setId(1L);

        when(panelService.createPanel(any(PanelProfileRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/panels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetAllPanels_Success() throws Exception {
        PanelProfileResponseDTO response = new PanelProfileResponseDTO();
        response.setId(1L);

        when(panelService.getAllPanels()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/panels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void testUpdatePanel_Success() throws Exception {
        PanelProfileRequestDTO dto = new PanelProfileRequestDTO();
        dto.setOrganization("NucleusTeq");

        PanelProfileResponseDTO response = new PanelProfileResponseDTO();
        response.setId(1L);

        when(panelService.updatePanel(anyLong(), any(PanelProfileRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/panels/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeletePanel_Success() throws Exception {
        doNothing().when(panelService).deletePanel(anyLong());

        mockMvc.perform(delete("/api/panels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
