package com.mahak.capstone.interviewprocesstrackingsystem.dto;

// Generic response structure for all APIs
public class ApiResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;

    // Constructor
    public ApiResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}