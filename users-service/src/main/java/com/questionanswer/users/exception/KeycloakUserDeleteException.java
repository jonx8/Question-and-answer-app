package com.questionanswer.users.exception;

public class KeycloakUserDeleteException extends RuntimeException {
    public KeycloakUserDeleteException(String message) {
        super(message);
    }

    public KeycloakUserDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
