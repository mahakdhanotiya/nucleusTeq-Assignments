package com.mahak.todo.todoapp.dto;

import java.time.LocalDateTime;

public class TodoResponseDTO {

    // Unique ID of the todo (generated from DB)
    private Long id;

    // Title of the task
    private String title;

    // Description of the task
    private String description;

    // Status of the task (e.g., PENDING, COMPLETED)
    private String status;

    // Time when the task was created
    private LocalDateTime createdAt;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}