package com.questionanswer.users.exception;

public class KeycloakUserCreationError extends RuntimeException {
    public KeycloakUserCreationError(String message) {
        super(message);
    }
}

