package com.questionanswer.questions.events;


import com.questionanswer.questions.entity.NotificationEventType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public abstract class NotificationEvent {
    private NotificationEventType eventType;
    private UUID actorId;
    private UUID userId;
    private Instant timestamp;
}
