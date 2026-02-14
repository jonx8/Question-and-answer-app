package com.questionanswer.users.exception;

import java.util.UUID;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public static ProfileNotFoundException withId(UUID id) {
        return new ProfileNotFoundException(id);
    }

}