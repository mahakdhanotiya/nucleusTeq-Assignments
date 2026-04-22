package com.mahak.capstone.interviewprocesstrackingsystem.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;

// This annotation makes this class handle exceptions globally across the project
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Logger for logging errors
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    
    // Handle Resource Not Found (404)

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleNotFound(ResourceNotFoundException ex) {

        logger.error("Resource not found: {}", ex.getMessage());

        return new ResponseEntity<>(
                new ApiResponseDTO<>(false, ex.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }



    // Handle Invalid Request (400)
    
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleBadRequest(InvalidRequestException ex) {

        logger.error("Invalid request: {}", ex.getMessage());

       return new ResponseEntity<>(
                new ApiResponseDTO<>(false, ex.getMessage(), null),
                HttpStatus.BAD_REQUEST
        );
    }


    
    // Handle All Other Exceptions (500)

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGeneric(Exception ex) {

        logger.error("Unexpected error: ", ex);

       return new ResponseEntity<>(
                new ApiResponseDTO<>(false, "Something went wrong", null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    } 
}