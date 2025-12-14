package com.example.web;

import com.example.exceptions.TemplateValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TemplateValidationException.class)
    public ResponseEntity<String> templateValidationException(TemplateValidationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
