package com.neueda.portfoliomanager.exceptions;

public class NoExistingUserException extends RuntimeException {
    public NoExistingUserException(String message) {
        super(message);
    }
}
