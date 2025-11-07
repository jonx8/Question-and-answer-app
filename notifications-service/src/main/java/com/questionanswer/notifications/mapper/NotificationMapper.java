package com.questionanswer.notifications.mapper;

import com.questionanswer.notifications.dto.CreateNotificationRequest;
import com.questionanswer.notifications.entity.Notification;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class NotificationMapper {
    public Notification toEntity(CreateNotificationRequest request) {
        return Notification.builder()
                .userId(request.userId())
                .actorId(request.userId())
                .type(request.type())
                .title(request.title())
                .message(request.message())
                .relatedData(request.relatedData())
                .isRead(false)
                .createdAt(Instant.now())
                .build();
    }

}
