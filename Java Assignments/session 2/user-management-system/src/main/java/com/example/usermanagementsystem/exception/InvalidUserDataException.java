package com.example.usermanagementsystem.exception;

// Custom exception thrown when user input data is invalid
public class InvalidUserDataException extends RuntimeException {

    public InvalidUserDataException(String message) {
        super(message);
    }
}