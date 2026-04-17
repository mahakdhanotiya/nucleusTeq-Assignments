package com.mahak.todo.todoapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.todo.todoapp.dto.TodoRequestDTO;
import com.mahak.todo.todoapp.dto.TodoResponseDTO;
import com.mahak.todo.todoapp.service.TodoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/todos")
public class TodoController {
    
    // service layer call
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    } 


    // POST API: handles POST request to create a new todo
    
    @PostMapping
    public TodoResponseDTO createTodo(@Valid @RequestBody TodoRequestDTO todoDTO) {
        
        // Call service layer and return response DTO
        return todoService.createTodo(todoDTO);
    }
    

    @GetMapping
    // GET API: Fetch all todos
    public List<TodoResponseDTO> getAllTodos() {

       // Call service layer to get all todos
       return todoService.getAllTodos();
    }


    @GetMapping("/{id}")
    // GET API: Fetch todo by ID
    public TodoResponseDTO getTodoById(@PathVariable Long id) {

       return todoService.getTodoById(id);
    }
}