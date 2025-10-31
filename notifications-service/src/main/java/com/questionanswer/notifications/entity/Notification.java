package com.questionanswer.notifications.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("notifications")
@Data
@Builder
public class Notification {
    @Id
    private UUID id;

    private UUID userId;
    private NotificationType type;
    private String title;
    private String message;
    private String relatedContentId;
    private boolean isRead;
    private Instant readAt;
    private Instant createdAt;
    private Instant updatedAt;


    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = Instant.now();
            this.updatedAt = Instant.now();
        }
    }

    public void markAsUnread() {
        if (this.isRead) {
            this.isRead = false;
            this.readAt = Instant.now();
            this.updatedAt = Instant.now();
        }
    }
}