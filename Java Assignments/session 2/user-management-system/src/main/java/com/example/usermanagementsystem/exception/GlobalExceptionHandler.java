package com.example.usermanagementsystem.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle UserNotFoundException
    
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> handleUserNotFound(UserNotFoundException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());

        return error;
    }

    // Handle UserAlreadyExistsException

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Map<String, String> handleUserAlreadyExists(UserAlreadyExistsException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());

        return error;
    }

    // Handle InvalidUserDataException

    @ExceptionHandler(InvalidUserDataException.class)
    public Map<String, String> handleInvalidUserData(InvalidUserDataException ex) {

       Map<String, String> error = new HashMap<>();
       error.put("message", ex.getMessage());

       return error;
    }
}