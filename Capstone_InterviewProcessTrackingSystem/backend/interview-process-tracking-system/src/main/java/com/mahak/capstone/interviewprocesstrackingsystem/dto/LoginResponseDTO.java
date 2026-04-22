package com.mahak.capstone.interviewprocesstrackingsystem.dto;

public class LoginResponseDTO {

    private String token;
    

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token) {
        this.token = token;
        
    }

    // getters
    public String getToken() {
        return token;
    }


    // setters
    public void setToken(String token) {
        this.token = token;
    }
}
