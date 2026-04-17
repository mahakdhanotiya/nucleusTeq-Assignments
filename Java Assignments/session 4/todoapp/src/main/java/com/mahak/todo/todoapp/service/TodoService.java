package com.mahak.todo.todoapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mahak.todo.todoapp.dto.TodoRequestDTO;
import com.mahak.todo.todoapp.dto.TodoResponseDTO;
import com.mahak.todo.todoapp.entity.Status;
import com.mahak.todo.todoapp.entity.Todo;
import com.mahak.todo.todoapp.exception.InvalidStatusTransitionException;
import com.mahak.todo.todoapp.exception.TodoNotFoundException;
import com.mahak.todo.todoapp.mapper.TodoMapper;
import com.mahak.todo.todoapp.repository.TodoRepository;

@Service
public class TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);
    
     //constructor injection
     private final TodoRepository todoRepository;
 
     public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }


    // CREATE API: Add a new todo

     public TodoResponseDTO createTodo(TodoRequestDTO todoDTO) {

        logger.info("Creating new todo with title: {}", todoDTO.getTitle());


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


        // set current timestamp
        todo.setCreatedAt(LocalDateTime.now());

        logger.info("Saving todo to database");

        // save todo to database
       Todo savedTodo = todoRepository.save(todo); 

       // Convert Entity to ResponseDTO
       TodoResponseDTO response = TodoMapper.toResponseDTO(savedTodo);
       return response;
    }



    // GET API: Fetch all todos and convert to ResponseDTO list
     public List<TodoResponseDTO> getAllTodos() {

       logger.info("Fetching all todos from database");

       // Fetch all todo entities from database
       List<Todo> todos = todoRepository.findAll();

       // Convert Entity → DTO using mapper
       return todos.stream()
        .map(TodoMapper::toResponseDTO)
        .collect(Collectors.toList());
    }



    // GET API: Fetch todo by ID and convert to ResponseDTO
    public TodoResponseDTO getTodoById(Long id) {

        logger.info("Fetching todo with id: {}", id);

        Optional<Todo> optionalTodo = todoRepository.findById(id);

       // If not found → throw exception
       if (optionalTodo.isEmpty()) {
           logger.error("Todo not found with id: {}", id);
           throw new TodoNotFoundException("Todo not found with id: " + id);
        }

       // Convert to DTO using mapper
       return TodoMapper.toResponseDTO(optionalTodo.get());
    }



    // UPDATE API: Update todo by ID
    public TodoResponseDTO updateTodo(Long id, TodoRequestDTO todoDTO) {

        logger.info("Updating todo with id: {}", id);

        Optional<Todo> optionalTodo = todoRepository.findById(id);

        if (optionalTodo.isEmpty()) {
            logger.error("Todo not found with id: {}", id);
            throw new TodoNotFoundException("Todo not found with id: " + id);
        }

        Todo todo = optionalTodo.get();

        // Update fields
        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());

        Status currentStatus = todo.getStatus();

        String statusStr = todoDTO.getStatus();

        Status newStatus;

        if (statusStr == null || statusStr.isBlank()) {
           newStatus = Status.PENDING;
        } else {
           newStatus = Status.valueOf(statusStr.toUpperCase());
        }

        // Validate transition
        if ((currentStatus == Status.PENDING && newStatus == Status.COMPLETED) ||
            (currentStatus == Status.COMPLETED && newStatus == Status.PENDING)) {

            todo.setStatus(newStatus);

        } else {
            logger.error("Invalid status transition for todo id: {}", id);
            throw new InvalidStatusTransitionException("Invalid status transition:" + currentStatus + "->" +  newStatus);
        }

        Todo updatedTodo = todoRepository.save(todo);

        return TodoMapper.toResponseDTO(updatedTodo);
    }



    // DELETE API: Delete todo by ID
    public void deleteTodo(Long id) {

        logger.info("Deleting todo with id: {}", id);


        Optional<Todo> optionalTodo = todoRepository.findById(id);

        if (optionalTodo.isEmpty()) {
           logger.error("Todo not found with id: {}", id);
           throw new TodoNotFoundException("Todo not found with id: " + id);
        }

        todoRepository.deleteById(id);
    }
}
