package com.example.web;

import com.example.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TemplateValidationException.class)
    public ResponseEntity<String> templateValidationException(TemplateValidationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<String> emailException(EmailException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RelationException.class)
    public ResponseEntity<String> relationException(RelationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(ApprovalException.class)
    public ResponseEntity<String> approvalException(ApprovalException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(ApprovalChainException.class)
    public ResponseEntity<String> approvalChainException(ApprovalChainException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> userException(UserException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}