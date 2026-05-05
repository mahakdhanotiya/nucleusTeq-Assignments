package com.mahak.capstone.interviewprocesstrackingsystem.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not found", response.getBody().getMessage());
    }

    @Test
    void testHandleBadRequest() {
        InvalidRequestException ex = new InvalidRequestException("Bad request");
        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleBadRequest(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", response.getBody().getMessage());
    }

    @Test
    void testHandleGeneric() {
        Exception ex = new Exception("Error");
        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Server Error: Error", response.getBody().getMessage());
    }

    @Test
    void testHandleValidationErrors() {
        org.springframework.web.bind.MethodArgumentNotValidException ex = mock(org.springframework.web.bind.MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult result = mock(org.springframework.validation.BindingResult.class);
        org.springframework.validation.FieldError error = new org.springframework.validation.FieldError("obj", "field", "must not be null");
        
        when(ex.getBindingResult()).thenReturn(result);
        when(result.getFieldErrors()).thenReturn(java.util.List.of(error));
        
        ResponseEntity<ApiResponseDTO<Void>> response = handler.handleValidationErrors(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("field: must not be null", response.getBody().getMessage());
    }
}
