package com.questionanswer.notifications.service.impl;

import com.questionanswer.notifications.dto.NotificationsListResponse;
import com.questionanswer.notifications.entity.Notification;
import com.questionanswer.notifications.exception.NotificationAlreadyReadException;
import com.questionanswer.notifications.exception.NotificationNotFoundException;
import com.questionanswer.notifications.repository.NotificationRepository;
import com.questionanswer.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;


    /**
     * Retrieves a notification by its identifier.
     *
     * @param id the ID of the notification to retrieve
     * @return the found notification entity
     * @throws NotificationNotFoundException if no notification exists with the given ID
     */
    @Override
    public Notification getNotificationById(String id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> NotificationNotFoundException.withId(id));
    }

    /**
     * Retrieves a paginated list of notifications for a specific user,
     * sorted by creation date in descending order (newest first).
     *
     * @param userId   the UUID of the user
     * @param pageable pagination and sorting parameters
     * @return page of user notifications
     */
    @Override
    public NotificationsListResponse getNotificationsByUserId(UUID userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return new NotificationsListResponse(
                page.stream().toList(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious()
        );

    }


    /**
     * Retrieves all unread notifications for a specific user.
     *
     * @param userId the UUID of the user
     * @return list of unread notifications
     */
    @Override
    public List<Notification> getUnreadNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    /**
     * Creates a new notification.
     *
     * @param notification the notification entity to create
     * @return the created notification with generated ID
     */
    @Override
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    /**
     * Marks a notification as read.
     * Throws an exception if the notification is already read.
     * Automatically updates the updatedAt timestamp
     *
     * @param id the ID of the notification to mark as read
     * @return the updated notification entity
     * @throws NotificationAlreadyReadException if the notification is already read
     * @throws NotificationNotFoundException    if the notification doesn't exist
     */
    @Override
    public Notification markAsRead(String id) {
        Notification notification = getNotificationById(id);
        if (notification.isRead()) {
            throw NotificationAlreadyReadException.withId(id);
        }
        notification.markAsRead();
        return notificationRepository.save(notification);
    }


    /**
     * Deletes a notification by its identifier.
     *
     * @param id the ID of the notification to delete
     * @throws NotificationNotFoundException if the notification doesn't exist
     */
    @Override
    public void deleteNotification(String id) {
        if (!notificationRepository.existsById(id)) {
            throw NotificationNotFoundException.withId(id);
        }
        notificationRepository.deleteById(id);
    }

}
