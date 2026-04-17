package com.mahak.todo.todoapp.mapper;

import com.mahak.todo.todoapp.dto.TodoResponseDTO;
import com.mahak.todo.todoapp.entity.Todo;

public class TodoMapper {

    // Convert Entity → ResponseDTO
    public static TodoResponseDTO toResponseDTO(Todo todo) {

        TodoResponseDTO response = new TodoResponseDTO();

        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setStatus(todo.getStatus().name());
        response.setCreatedAt(todo.getCreatedAt());

        return response;
    }
}