package com.questionanswer.notifications.service;

import com.questionanswer.notifications.dto.NotificationsListResponse;
import com.questionanswer.notifications.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface NotificationService {
    Notification getNotificationById(String id);

    Notification createNotification(Notification notification);

    NotificationsListResponse getNotificationsByUserId(UUID userId, Pageable pageable);

    List<Notification> getUnreadNotificationsByUserId(UUID userId);

    Notification markAsRead(String id);

    void deleteNotification(String id);
}
