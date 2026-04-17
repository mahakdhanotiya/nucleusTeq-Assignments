package com.mahak.todo.todoapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "todo_items") // Database table name
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    // Stores creation time of the task
    private LocalDateTime createdAt;


    // No-args constructor (required by JPA)
    public Todo() {
    }

    // All-args constructor
    public Todo(Long id, String title, String description, Status status, LocalDateTime createdAt) {
         this.id = id;
         this.title = title;
         this.description = description;
         this.status = status;
         this.createdAt = createdAt;

    }


    // Getter & Setter for id
     public Long getId(){
         return id;
     }
    
     public void setId(Long id){
         this.id=id;
     }


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
    public Status getStatus() {
         return status;
    }

    public void setStatus(Status status) {
         this.status = status;
    }


    // Getter & Setter for createdAt
    public LocalDateTime getCreatedAt() {
         return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
         this.createdAt = createdAt;
    }
    
}



