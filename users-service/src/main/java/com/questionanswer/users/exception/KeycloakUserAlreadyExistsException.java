package com.questionanswer.users.exception;

public class KeycloakUserAlreadyExistsException extends RuntimeException {
    public KeycloakUserAlreadyExistsException(String message) {
        super(message);
    }
}