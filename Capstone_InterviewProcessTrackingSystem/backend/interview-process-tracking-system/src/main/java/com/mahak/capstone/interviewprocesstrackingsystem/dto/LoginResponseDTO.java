package com.mahak.capstone.interviewprocesstrackingsystem.dto;

public class LoginResponseDTO {

    private String token;
    private String role;
    private Long userId;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, String role, Long userId) {
        this.token = token;
        this.role = role;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}