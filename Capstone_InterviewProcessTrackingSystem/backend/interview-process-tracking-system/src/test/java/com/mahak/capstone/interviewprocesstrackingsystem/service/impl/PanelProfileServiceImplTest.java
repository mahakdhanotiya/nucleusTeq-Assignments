package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.PanelProfileMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PanelProfileRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PasswordTokenRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.service.EmailService;
import com.mahak.capstone.interviewprocesstrackingsystem.validation.PanelProfileValidation;

@ExtendWith(MockitoExtension.class)
class PanelProfileServiceImplTest {

    @Mock private PanelProfileRepository panelRepository;
    @Mock private UserRepository userRepository;
    @Mock private PanelProfileMapper panelMapper;
    @Mock private PanelProfileValidation panelValidation;
    @Mock private EmailService emailService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PasswordTokenRepository passwordTokenRepository;
    @Mock private JdbcTemplate jdbcTemplate;

    @InjectMocks private PanelProfileServiceImpl panelService;

    @Test
    void createPanel_UserExists_DuplicateCheck() {
        PanelProfileRequestDTO dto = new PanelProfileRequestDTO();
        dto.setEmail("exists@test.com");
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.findByEmail("exists@test.com")).thenReturn(Optional.of(user));
        when(panelRepository.existsByUserId(1L)).thenReturn(true);
        assertThrows(InvalidRequestException.class, () -> panelService.createPanel(dto));
    }

    @Test
    void createPanel_NewUser_Success() {
        PanelProfileRequestDTO dto = new PanelProfileRequestDTO();
        dto.setEmail("new@test.com");
        dto.setFullName("New Panel");
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(new User());
        PanelProfile panel = new PanelProfile();
        ReflectionTestUtils.setField(panel, "id", 10L);
        when(panelMapper.toEntity(any(), any())).thenReturn(panel);
        when(panelRepository.save(any())).thenReturn(panel);
        when(panelMapper.toResponseDTO(any())).thenReturn(new PanelProfileResponseDTO());
        panelService.createPanel(dto);
        verify(emailService).sendPanelOnboardingEmail(any(), any(), any());
    }

    @Test
    void getPanelById_NotFound_Exception() {
        when(panelRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> panelService.getPanelById(1L));
    }

    @Test
    void getAllPanels_Success() {
        when(panelRepository.findAll()).thenReturn(List.of(new PanelProfile()));
        List<PanelProfileResponseDTO> list = panelService.getAllPanels();
        assertEquals(1, list.size());
    }

    @Test
    void updatePanel_Success() {
        PanelProfile existing = new PanelProfile();
        User user = new User();
        existing.setUser(user);
        when(panelRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(panelRepository.save(any())).thenReturn(existing);
        when(panelMapper.toResponseDTO(any())).thenReturn(new PanelProfileResponseDTO());
        PanelProfileRequestDTO dto = new PanelProfileRequestDTO();
        dto.setOrganization("Org");
        dto.setEmail("new@email.com");
        panelService.updatePanel(1L, dto);
        verify(userRepository).save(user);
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    void updatePanel_NotFound_Exception() {
        when(panelRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> panelService.updatePanel(1L, new PanelProfileRequestDTO()));
    }

    @Test
    void deletePanel_NotFound_Exception() {
        when(panelRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> panelService.deletePanel(1L));
    }

    @Test
    void deletePanel_HandleCleanupExceptions() {
        PanelProfile panel = new PanelProfile();
        panel.setUser(new User());
        when(panelRepository.findById(1L)).thenReturn(Optional.of(panel));
        when(jdbcTemplate.update(anyString(), any(Object.class))).thenThrow(new RuntimeException("DB Error"));
        doThrow(new RuntimeException("Delete Error")).when(userRepository).delete(any());
        assertDoesNotThrow(() -> panelService.deletePanel(1L));
        verify(panelRepository).delete(panel);
    }
}
