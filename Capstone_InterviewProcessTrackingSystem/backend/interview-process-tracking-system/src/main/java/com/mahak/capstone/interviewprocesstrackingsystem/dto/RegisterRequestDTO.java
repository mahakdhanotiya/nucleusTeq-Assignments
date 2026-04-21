package com.mahak.capstone.interviewprocesstrackingsystem.dto;

public class RegisterRequestDTO {

    private String fullName;
    private String email;
    private String password;

    // getters
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // setters
    public void setFullName(String fullName) {
        this.fullName = fullName;

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
