package com.mahak.capstone.interviewprocesstrackingsystem.exception;

/*This exception is used for invalid input / bad requests 
*/
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}