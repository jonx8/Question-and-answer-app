package com.questionanswer.notifications.mapper;

import com.questionanswer.notifications.entity.Notification;
import com.questionanswer.notifications.events.AnswerCreatedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class NotificationMapper {
    public Notification toEntity(AnswerCreatedEvent event) {
        return Notification.builder()
                .title("New Answer to Your Question")
                .message(String.format("%s answered your question \"%s\"", event.getAnswerId(), event.getQuestionTitle()))
                .actorId(event.getActorId())
                .userId(event.getUserId())
                .type(event.getEventType())
                .relatedData(Map.of(
                        "questionId", event.getQuestionId(),
                        "questionTitle", event.getQuestionTitle(),
                        "answerId", event.getAnswerId()
                ))
                .createdAt(event.getTimestamp() != null ?
                        event.getTimestamp() : Instant.now())
                .isRead(false)
                .build();
    }
}
