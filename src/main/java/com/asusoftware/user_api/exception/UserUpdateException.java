package com.asusoftware.user_api.exception;

public class UserUpdateException extends RuntimeException {
    public UserUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}