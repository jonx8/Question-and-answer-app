package com.questionanswer.notifications.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Document(collection = "notifications")
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(name = "receiver_created_idx", def = "{'receiverId': 1, 'createdAt': -1}")
public class Notification {
    @Id
    private String id;

    private UUID actorId;

    @Indexed
    private UUID userId;

    private NotificationType type;
    private String title;
    private String message;
    private Map<String, Object> relatedData;
    private boolean isRead;
    private Instant readAt;

    @CreatedDate
    @Indexed(expireAfter = "1d")
    private Instant createdAt;

    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = Instant.now();
        }
    }
}