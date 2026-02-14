package com.questionanswer.users.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        Instant createdAt
) {
}
