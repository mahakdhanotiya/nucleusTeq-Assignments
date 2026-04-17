package com.mahak.todo.todoapp.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahak.todo.todoapp.dto.TodoRequestDTO;
import com.mahak.todo.todoapp.dto.TodoResponseDTO;
import com.mahak.todo.todoapp.entity.Status;
import com.mahak.todo.todoapp.entity.Todo;
import com.mahak.todo.todoapp.repository.TodoRepository;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository; // repository for DB operations

     public TodoResponseDTO createTodo(TodoRequestDTO todoDTO) {

        Todo todo = new Todo(); // creating new entity object

       
        // mapping DTO fields to entity
        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());

        String statusStr = todoDTO.getStatus();

        // basic validation for status
        if (statusStr == null || statusStr.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or empty");

        }

        // convert String to Enum
        Status status = Status.valueOf(statusStr.toUpperCase());
        todo.setStatus(status);


        // setting current timestamp
        todo.setCreatedAt(LocalDateTime.now());

        // saving todo to database
       Todo savedTodo = todoRepository.save(todo); 

       // Entity → ResponseDTO
       TodoResponseDTO response = new TodoResponseDTO();
       response.setId(savedTodo.getId());
       response.setTitle(savedTodo.getTitle());
       response.setDescription(savedTodo.getDescription());
       response.setStatus(savedTodo.getStatus().name());
       response.setCreatedAt(savedTodo.getCreatedAt());

       return response;
    }
}

