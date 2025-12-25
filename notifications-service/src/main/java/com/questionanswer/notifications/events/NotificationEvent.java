package com.questionanswer.notifications.events;


import com.questionanswer.notifications.entity.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public abstract class NotificationEvent {
    private NotificationType eventType;
    private UUID actorId;
    private UUID userId;
    private Instant timestamp;
}
