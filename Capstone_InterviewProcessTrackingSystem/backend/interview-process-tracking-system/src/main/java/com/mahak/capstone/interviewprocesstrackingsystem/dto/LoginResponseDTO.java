package com.mahak.capstone.interviewprocesstrackingsystem.dto;

public class LoginResponseDTO {

    private String token;
    private String role;
    private Long userId;
    private String fullName;
    private Long profileId; // For Candidates or Panels

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, String role, Long userId, Long profileId, String fullName) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.profileId = profileId;
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }
}