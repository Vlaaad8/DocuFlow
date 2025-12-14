package com.example.exceptions;

public class TemplateValidationException extends RuntimeException {
    public TemplateValidationException(String message) {
        super(message);
    }
}
