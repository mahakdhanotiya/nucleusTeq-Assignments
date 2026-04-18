package com.mahak.todo.todoapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.mahak.todo.todoapp.component.NotificationServiceClient;
import com.mahak.todo.todoapp.dto.TodoRequestDTO;
import com.mahak.todo.todoapp.entity.Status;
import com.mahak.todo.todoapp.entity.Todo;
import com.mahak.todo.todoapp.exception.InvalidStatusTransitionException;
import com.mahak.todo.todoapp.exception.TodoNotFoundException;
import com.mahak.todo.todoapp.repository.TodoRepository;

public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private TodoService todoService;

    public TodoServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSetup() {
        System.out.println("Test setup working");
    }

    // ---------- CREATE TODO TESTS ----------

    @Test
    void testCreateTodo() {

    // input DTO
       TodoRequestDTO dto = new TodoRequestDTO();
       dto.setTitle("Test Task");
       dto.setDescription("Test Desc");
       dto.setStatus("pending");

       // fake saved entity
       Todo savedTodo = new Todo();
       savedTodo.setId(1L);
       savedTodo.setTitle("Test Task");
       savedTodo.setDescription("Test Desc");
       savedTodo.setStatus(Status.PENDING);

       when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

       var response = todoService.createTodo(dto);

       assertNotNull(response);
       assertEquals("Test Task", response.getTitle());

       verify(todoRepository, times(1)).save(any(Todo.class));
       verify(notificationServiceClient, times(1))
               .sendNotification(anyString());
    }

    // ---------- GET TODO BY ID TESTS ----------

    @Test
    void testGetTodoById() {

       Long id = 1L;

       Todo todo = new Todo();
       todo.setId(id);
       todo.setTitle("Test Task");
       todo.setDescription("Test Desc");
       todo.setStatus(Status.PENDING);

       when(todoRepository.findById(id))
               .thenReturn(java.util.Optional.of(todo));

       var response = todoService.getTodoById(id);

       assertNotNull(response);
       assertEquals("Test Task", response.getTitle());

       verify(todoRepository, times(1)).findById(id);
    }


    @Test
    void testGetTodoById_NotFound() {

       Long id = 1L;

       when(todoRepository.findById(id))
               .thenReturn(java.util.Optional.empty());

       assertThrows(TodoNotFoundException.class, () -> {
           todoService.getTodoById(id);
       });

       verify(todoRepository, times(1)).findById(id);
    }

    // ---------- GET ALL TODOS TEST ----------

    @Test
    void testGetAllTodos() {

       // fake data
       Todo todo1 = new Todo();
       todo1.setId(1L);
       todo1.setTitle("Task 1");
       todo1.setDescription("Desc 1");
       todo1.setStatus(Status.PENDING);

       Todo todo2 = new Todo();
       todo2.setId(2L);
       todo2.setTitle("Task 2");
       todo2.setDescription("Desc 2");
       todo2.setStatus(Status.COMPLETED);

       when(todoRepository.findAll())
               .thenReturn(java.util.List.of(todo1, todo2));

       var response = todoService.getAllTodos();

       assertNotNull(response);
       assertEquals(2, response.size());
       assertEquals("Task 1", response.get(0).getTitle());

       verify(todoRepository, times(1)).findAll();
    }

    // ---------- UPDATE TODO TESTS ----------

   @Test
    void testUpdateTodo() {
        Long id = 1L;

       // existing todo
       Todo existing = new Todo();
       existing.setId(id);
       existing.setTitle("Old Title");
       existing.setDescription("Old Desc");
       existing.setStatus(Status.PENDING);

       // update input
       TodoRequestDTO dto = new TodoRequestDTO();
       dto.setTitle("New Title");
       dto.setDescription("New Desc");
       dto.setStatus("completed");

       when(todoRepository.findById(id))
               .thenReturn(java.util.Optional.of(existing));

       when(todoRepository.save(any(Todo.class)))
               .thenReturn(existing);

       var response = todoService.updateTodo(id, dto);

       assertEquals("New Title", response.getTitle());

       verify(todoRepository, times(1)).findById(id);
       verify(todoRepository, times(1)).save(any(Todo.class));
    }


    @Test
    void testUpdateTodo_NotFound() {
        Long id = 1L;

        when(todoRepository.findById(id))
               .thenReturn(java.util.Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> {
           todoService.updateTodo(id, new TodoRequestDTO());
       });

        verify(todoRepository, times(1)).findById(id);
     }


     @Test
     void testUpdateTodo_InvalidStatusTransition() {
        Long id = 1L;

        Todo existing = new Todo();
        existing.setId(id);
        existing.setStatus(Status.PENDING);

        TodoRequestDTO dto = new TodoRequestDTO();
        dto.setStatus("pending"); // invalid transition (same state)

        when(todoRepository.findById(id))
            .thenReturn(java.util.Optional.of(existing));

        assertThrows(InvalidStatusTransitionException.class, () -> {
           todoService.updateTodo(id, dto);
        });

        verify(todoRepository, times(1)).findById(id);
    }

    // ---------- DELETE TODO TESTS ----------

    @Test
    void testDeleteTodo() {

        Long id = 1L;

        Todo todo = new Todo();
        todo.setId(id);

        when(todoRepository.findById(id))
                .thenReturn(java.util.Optional.of(todo));

        doNothing().when(todoRepository).deleteById(id);

        todoService.deleteTodo(id);

        verify(todoRepository, times(1)).findById(id);
        verify(todoRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteTodo_NotFound() {

        Long id = 1L;

        when(todoRepository.findById(id))
                .thenReturn(java.util.Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> {
            todoService.deleteTodo(id);
        });

        verify(todoRepository, times(1)).findById(id);
    }
 }


