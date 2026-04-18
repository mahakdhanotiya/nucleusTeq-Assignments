package com.mahak.todo.todoapp.mapper;

import com.mahak.todo.todoapp.dto.TodoRequestDTO;
import com.mahak.todo.todoapp.dto.TodoResponseDTO;
import com.mahak.todo.todoapp.entity.Status;
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


    public static Todo toEntity(TodoRequestDTO dto) {

        Todo todo = new Todo();

        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());

        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            todo.setStatus(Status.PENDING);
        } else {
            todo.setStatus(Status.valueOf(dto.getStatus().toUpperCase()));
        }

        return todo;
    }
}