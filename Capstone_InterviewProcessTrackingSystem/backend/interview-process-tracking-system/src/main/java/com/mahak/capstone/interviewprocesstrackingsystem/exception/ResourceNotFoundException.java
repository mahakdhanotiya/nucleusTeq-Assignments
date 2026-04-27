package com.mahak.capstone.interviewprocesstrackingsystem.exception;

/* This exception is used when a resource (like user) is not found in DB
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}