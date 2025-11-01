package com.questionanswer.notifications.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.questionanswer.notifications.entity.NotificationType;
import jakarta.validation.constraints.Size;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateNotificationRequest(
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @Size(max = 2000, message = "Message must not exceed 2000 characters")
        String message,

        Boolean isRead,

        NotificationType type,

        @Size(max = 100, message = "Related content ID must not exceed 100 characters")
        String relatedContentId
) {
    public boolean hasUpdates() {
        return title != null || message != null || isRead != null || relatedContentId != null;
    }

}