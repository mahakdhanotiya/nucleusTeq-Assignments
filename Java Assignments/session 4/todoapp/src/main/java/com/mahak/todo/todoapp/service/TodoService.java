package com.mahak.todo.todoapp.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.mahak.todo.todoapp.dto.TodoRequestDTO;
import com.mahak.todo.todoapp.dto.TodoResponseDTO;
import com.mahak.todo.todoapp.entity.Status;
import com.mahak.todo.todoapp.entity.Todo;
import com.mahak.todo.todoapp.mapper.TodoMapper;
import com.mahak.todo.todoapp.repository.TodoRepository;

@Service
public class TodoService {
    
     //constructor injection
     private final TodoRepository todoRepository;
 
     public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

     public TodoResponseDTO createTodo(TodoRequestDTO todoDTO) {

        Todo todo = new Todo(); // creating new entity object

       
        // mapping DTO fields to entity
        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());

        String statusStr = todoDTO.getStatus();

        Status status;

        //setting default value for status
        if (statusStr == null || statusStr.isBlank()) {
        
        status = Status.PENDING;
        }
         else {
        status = Status.valueOf(statusStr.toUpperCase());
        }

        todo.setStatus(status);


        // setting current timestamp
        todo.setCreatedAt(LocalDateTime.now());

        // saving todo to database
       Todo savedTodo = todoRepository.save(todo); 

       // Entity → ResponseDTO
       TodoResponseDTO response = TodoMapper.toResponseDTO(savedTodo);
       return response;
    }
}
