package com.example.user_search_system.exception;


 // Custom exception for invalid user input

public class InvalidUserException extends RuntimeException {

    public InvalidUserException(String message) {
        super(message);
    }
}