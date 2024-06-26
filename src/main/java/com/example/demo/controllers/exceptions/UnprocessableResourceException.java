package com.example.demo.controllers.exceptions;

public class UnprocessableResourceException extends RuntimeException {
    public UnprocessableResourceException(String message) {
        super(message);
    }
}
