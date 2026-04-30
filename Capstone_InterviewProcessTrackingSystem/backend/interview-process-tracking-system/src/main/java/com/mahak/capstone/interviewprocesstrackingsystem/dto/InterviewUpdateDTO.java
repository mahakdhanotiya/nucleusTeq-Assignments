package com.mahak.capstone.interviewprocesstrackingsystem.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InterviewUpdateDTO {

    @NotNull(message = "Interview date & time is required")
    private LocalDateTime interviewDateTime;

    @NotBlank(message = "Focus area is required")
    @Size(max = 500, message = "Focus area cannot exceed 500 characters")
    private String focusArea;

    public LocalDateTime getInterviewDateTime() {
        return interviewDateTime;
    }

    public void setInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }
}
