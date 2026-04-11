package com.example.usermanagementsystem.exception;

// Custom exception thrown when trying to add a user with an existing ID
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}