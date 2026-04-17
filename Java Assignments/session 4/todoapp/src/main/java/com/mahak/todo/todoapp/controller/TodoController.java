package com.mahak.todo.todoapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.todo.todoapp.dto.TodoDTO;
import com.mahak.todo.todoapp.entity.Todo;
import com.mahak.todo.todoapp.service.TodoService;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoService todoService; // service layer call


    // handles POST request to create a new todo
    
    @PostMapping
    public Todo createTodo(@RequestBody TodoDTO todoDTO) {
        return todoService.createTodo(todoDTO);
    }
}