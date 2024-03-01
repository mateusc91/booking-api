package com.example.bookingtechtest.exception;

public class OverlapedBookingException extends RuntimeException {
    public OverlapedBookingException(String message) {
        super(message);
    }
}

