package com.mahak.todo.todoapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TodoDTO {

    @NotNull
    @Size(min = 3)
    private String title;

    @Size(max = 200)
    private String description;

    private String status;

    // Getter & Setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter & Setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter & Setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
    this.status = status;
    }
}
