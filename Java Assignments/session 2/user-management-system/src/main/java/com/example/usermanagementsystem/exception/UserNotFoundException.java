package com.example.usermanagementsystem.exception;

// Custom exception thrown when user is not found
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}