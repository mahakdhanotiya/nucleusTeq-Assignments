package com.example.user_search_system.exception;


 // Exception for duplicate user
 
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
