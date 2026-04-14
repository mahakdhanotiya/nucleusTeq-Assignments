package com.example.user_search_system.exception;


 // Exception for user not found

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}