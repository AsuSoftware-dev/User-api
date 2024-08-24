package com.asusoftware.user_api.exception;

public class UserCreationException extends RuntimeException {
    public UserCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}