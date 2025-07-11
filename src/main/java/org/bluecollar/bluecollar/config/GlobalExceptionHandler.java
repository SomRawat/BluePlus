package org.bluecollar.bluecollar.config;

import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public BlueCollarApiResponse<String> handleRuntimeException(RuntimeException ex) {
        if ("Session not valid".equals(ex.getMessage())) {
            return new BlueCollarApiResponse<>("Session not valid", 500);
        }
        return new BlueCollarApiResponse<>(ex.getMessage(), 400);
    }
    
    @ExceptionHandler(Exception.class)
    public BlueCollarApiResponse<String> handleGenericException(Exception ex) {
        return new BlueCollarApiResponse<>("Internal server error", 500);
    }
    

}