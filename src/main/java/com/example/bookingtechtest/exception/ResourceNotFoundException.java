package com.example.bookingtechtest.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

