package com.example.exceptions;

public class EmailException extends RuntimeException {
    public EmailException(String message) {
        super(message);
    }
}
