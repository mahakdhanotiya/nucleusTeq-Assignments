package com.mahak.capstone.interviewprocesstrackingsystem.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;

/**
 * Global exception handler to manage all application exceptions
 * and return consistent API responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Logger for logging errors
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    
    /**
     * Handles ResourceNotFoundException and returns HTTP 404 response.
     */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleNotFound(ResourceNotFoundException ex) {

        logger.error("Resource not found: {}", ex.getMessage());

        return new ResponseEntity<>(
                new ApiResponseDTO<>(false, ex.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }

    
    /**
    * Handles InvalidRequestException and returns HTTP 400 response.
    */
    
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleBadRequest(InvalidRequestException ex) {

        logger.error("Invalid request: {}", ex.getMessage());

       return new ResponseEntity<>(
                new ApiResponseDTO<>(false, ex.getMessage(), null),
                HttpStatus.BAD_REQUEST
        );
    }


    
    /**
    * Handles all uncaught exceptions and returns HTTP 500 response.
    */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGeneric(Exception ex) {
        logger.error("Unexpected error: ", ex);
       return new ResponseEntity<>(
                new ApiResponseDTO<>(false, "Server Error: " + ex.getMessage(), null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    } 

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponseDTO<Void>> handleValidationErrors(
        org.springframework.web.bind.MethodArgumentNotValidException ex) {

    String message = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(java.util.stream.Collectors.joining(", "));

    logger.error("Validation failed: {}", message);
    return new ResponseEntity<>(
            new ApiResponseDTO<>(false, message, null),
            HttpStatus.BAD_REQUEST
    );
}

}