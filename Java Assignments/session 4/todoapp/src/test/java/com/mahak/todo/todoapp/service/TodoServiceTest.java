package com.mahak.todo.todoapp.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mahak.todo.todoapp.component.NotificationServiceClient;
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
}