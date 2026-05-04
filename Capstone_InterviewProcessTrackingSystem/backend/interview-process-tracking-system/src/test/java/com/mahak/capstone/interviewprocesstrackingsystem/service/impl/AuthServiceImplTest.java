package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.RegisterRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.SetPasswordRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PasswordToken;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.Role;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.CandidateRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PanelProfileRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PasswordTokenRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;
import com.mahak.capstone.interviewprocesstrackingsystem.service.EmailService;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordTokenRepository passwordTokenRepository;
    @Mock private EmailService emailService;
    @Mock private CandidateRepository candidateRepository;
    @Mock private PanelProfileRepository panelRepository;

    @InjectMocks private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "frontendUrl", "http://test.com");
    }

    @Test
    void register_Success_HREmailSuffix() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("admin.hr@test.com");
        dto.setFullName("HR Admin");
        
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed_pass");
        
        authService.register(dto);
        
        verify(userRepository).save(argThat(u -> u.getRole() == Role.HR));
    }

    @Test
    void login_Success_Panel() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("panel@test.com");
        dto.setPassword("cGFzcw=="); // Base64 for "pass"

        User user = new User();
        user.setEmail("panel@test.com");
        user.setPassword("hashed_pass");
        user.setRole(Role.PANEL);
        user.setFullName("Panel User");

        PanelProfile panel = new PanelProfile();
        ReflectionTestUtils.setField(panel, "id", 100L);

        when(userRepository.findByEmail("panel@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hashed_pass")).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");
        when(panelRepository.findByUserEmail("panel@test.com")).thenReturn(Optional.of(panel));

        LoginResponseDTO response = authService.login(dto);
        assertEquals(100L, response.getProfileId());
    }

    @Test
    void register_Success_Candidate() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("candidate@test.com");
        dto.setFullName("Candidate User");
        dto.setRole(Role.CANDIDATE);

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed_pass");

        authService.register(dto);

        verify(userRepository).save(argThat(u -> u.getRole() == Role.CANDIDATE));
    }

    @Test
    void login_Success_Candidate() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("can@test.com");
        dto.setPassword("cGFzcw=="); // Base64 for "pass"

        User user = new User();
        user.setEmail("can@test.com");
        user.setPassword("hashed_pass");
        user.setRole(Role.CANDIDATE);
        user.setFullName("Candidate User");

        CandidateProfile candidate = new CandidateProfile();
        ReflectionTestUtils.setField(candidate, "id", 200L);

        when(userRepository.findByEmail("can@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hashed_pass")).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");
        when(candidateRepository.findByUserEmail("can@test.com")).thenReturn(Optional.of(candidate));

        LoginResponseDTO response = authService.login(dto);
        assertEquals(200L, response.getProfileId());
    }

    @Test
    void setPassword_Fail_ExpiredToken() {
        SetPasswordRequestDTO dto = new SetPasswordRequestDTO();
        dto.setToken("expired");
        PasswordToken token = new PasswordToken("u@t.com");
        token.setUsed(true);

        when(passwordTokenRepository.findByToken("expired")).thenReturn(Optional.of(token));
        assertThrows(InvalidRequestException.class, () -> authService.setPassword(dto));
    }

    @Test
    void getMe_Success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("me@test.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setEmail("me@test.com");
        when(userRepository.findByEmail("me@test.com")).thenReturn(Optional.of(user));

        User result = authService.getMe();
        assertEquals("me@test.com", result.getEmail());
    }

    @Test
    void login_UserNotFound_Exception() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("nonexistent@test.com");
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> authService.login(dto));
    }

    @Test
    void setPassword_TokenNotFound_Exception() {
        SetPasswordRequestDTO dto = new SetPasswordRequestDTO();
        dto.setToken("invalid");
        when(passwordTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());
        assertThrows(InvalidRequestException.class, () -> authService.setPassword(dto));
    }

    @Test
    void setPassword_UserNotFound_Exception() {
        SetPasswordRequestDTO dto = new SetPasswordRequestDTO();
        dto.setToken("valid");
        PasswordToken token = new PasswordToken("u@t.com");
        when(passwordTokenRepository.findByToken("valid")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("u@t.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> authService.setPassword(dto));
    }

    @Test
    void getMe_UserNotFound_Exception() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("unknown@test.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> authService.getMe());
    }

    @Test
    void register_WithDOBAndRole_Success() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("custom@test.com");
        dto.setFullName("Custom User");
        dto.setDateOfBirth("1995-05-15");
        dto.setRole(Role.PANEL);

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed");

        authService.register(dto);

        verify(userRepository).save(argThat(u -> 
            u.getDateOfBirth().toString().equals("1995-05-15") && 
            u.getRole() == Role.PANEL));
    }

    @Test
    void setPassword_Success() {
        SetPasswordRequestDTO dto = new SetPasswordRequestDTO();
        dto.setToken("valid");
        dto.setPassword("cGFzc3dvcmQxMjM="); // Base64 for "password123"

        PasswordToken token = new PasswordToken("u@t.com");
        User user = new User();
        user.setEmail("u@t.com");

        when(passwordTokenRepository.findByToken("valid")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("u@t.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        authService.setPassword(dto);

        verify(userRepository).save(user);
        assertTrue(token.isUsed());
    }
}
