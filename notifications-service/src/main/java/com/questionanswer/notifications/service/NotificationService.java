package com.questionanswer.notifications.service;

import com.questionanswer.notifications.controller.dto.CreateNotificationRequest;
import com.questionanswer.notifications.controller.dto.NotificationsListResponse;
import com.questionanswer.notifications.controller.dto.UpdateNotificationRequest;
import com.questionanswer.notifications.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public interface NotificationService {
    Notification getNotificationById(UUID id);

    long getUnreadNotificationsCount(UUID userId);

    Notification createNotification(CreateNotificationRequest request);

    NotificationsListResponse getNotificationsByUserId(UUID userId, Pageable pageable);

    List<Notification> getUnreadNotificationsByUserId(UUID userId);

    Notification markAsRead(UUID id);

    Notification updateNotification(UUID id, UpdateNotificationRequest request);

    void deleteNotification(UUID id);
}
