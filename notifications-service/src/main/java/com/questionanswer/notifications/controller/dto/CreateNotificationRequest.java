package com.questionanswer.notifications.controller.dto;

import com.questionanswer.notifications.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateNotificationRequest(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Notification type is required")
        NotificationType type,

        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        @Size(max = 2000, message = "Message must not exceed 2000 characters")
        String message,

        @Size(max = 100, message = "Related content ID must not exceed 100 characters")
        String relatedContentId
) {
}