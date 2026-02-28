package com.example.exceptions;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
    public UserException(int id) {
        super("User with id " + id + " not found");
    }
}
