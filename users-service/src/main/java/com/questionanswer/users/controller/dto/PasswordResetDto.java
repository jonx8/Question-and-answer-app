package com.questionanswer.users.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetDto(@NotBlank(message = "New password is required")
                               @Size(min = 6, max = 128, message = "Password must be between 6 and 128 characters") String newPassword) {
}
