package com.asusoftware.user_api.exception;

public class PlaceDeletionException extends RuntimeException {
    public PlaceDeletionException(String message) {
        super(message);
    }

    public PlaceDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}