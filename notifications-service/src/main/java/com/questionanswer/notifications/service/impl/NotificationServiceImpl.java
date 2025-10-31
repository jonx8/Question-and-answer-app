package com.questionanswer.notifications.service.impl;

import com.questionanswer.notifications.controller.dto.CreateNotificationRequest;
import com.questionanswer.notifications.controller.dto.NotificationsListResponse;
import com.questionanswer.notifications.controller.dto.UpdateNotificationRequest;
import com.questionanswer.notifications.entity.Notification;
import com.questionanswer.notifications.exception.NotificationAlreadyReadException;
import com.questionanswer.notifications.exception.NotificationNotFoundException;
import com.questionanswer.notifications.mapper.NotificationMapper;
import com.questionanswer.notifications.repository.NotificationRepository;
import com.questionanswer.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;


    /**
     * Retrieves a notification by its identifier.
     *
     * @param id the UUID of the notification to retrieve
     * @return the found notification entity
     * @throws NotificationNotFoundException if no notification exists with the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationById(UUID id) {
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    @Override
    public List<Notification> getUnreadNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    /**
     * Retrieves the count of unread notifications for a specific user.
     * Primarily used for displaying unread message badges.
     *
     * @param userId the UUID of the user
     * @return count of unread notifications
     */
    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationsCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }


    /**
     * Creates a new notification based on the provided request data.
     *
     * @param request DTO containing notification creation data
     * @return the created notification entity
     */
    @Override
    @Transactional
    public Notification createNotification(CreateNotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        return notificationRepository.save(notification);
    }

    /**
     * Marks a notification as read.
     * Throws an exception if the notification is already read.
     * Automatically updates the updatedAt timestamp
     *
     * @param id the UUID of the notification to mark as read
     * @return the updated notification entity
     * @throws NotificationAlreadyReadException if the notification is already read
     * @throws NotificationNotFoundException    if the notification doesn't exist
     */
    @Transactional
    @Override
    public Notification markAsRead(UUID id) {
        Notification notification = getNotificationById(id);
        if (notification.isRead()) {
            throw new NotificationAlreadyReadException(id);
        }
        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    /**
     * Applies partial updates to a notification.
     * Only non-null fields from the request are updated.
     * Automatically updates the updatedAt timestamp.
     *
     * @param id      the UUID of the notification to update
     * @param request DTO containing fields to modify
     * @return the updated notification entity
     * @throws NotificationNotFoundException if the notification doesn't exist
     */
    @Transactional
    @Override
    public Notification updateNotification(UUID id, UpdateNotificationRequest request) {
        if (!request.hasUpdates()) {
            log.warn("Update request for notification {} contains no changes", id);
            return getNotificationById(id);
        }

        Notification notification = getNotificationById(id);
        applyUpdates(notification, request);
        return notificationRepository.save(notification);
    }


    /**
     * Deletes a notification by its identifier.
     *
     * @param id the UUID of the notification to delete
     * @throws NotificationNotFoundException if the notification doesn't exist
     */
    @Transactional
    @Override
    public void deleteNotification(UUID id) {
        if (!notificationRepository.existsById(id)) {
            throw NotificationNotFoundException.withId(id);
        }
        notificationRepository.deleteById(id);
    }


    private void applyUpdates(Notification notification, UpdateNotificationRequest request) {
        if (request.title() != null) {
            notification.setTitle(request.title());
        }

        if (request.message() != null) {
            notification.setMessage(request.message());
        }

        if (request.type() != null) {
            notification.setType(request.type());
        }

        if (request.relatedContentId() != null) {
            notification.setRelatedContentId(request.relatedContentId());
        }
        if (request.isRead() != null) {
            if (request.isRead()) {
                notification.markAsRead();
            } else {
                notification.markAsUnread();
            }
        }

        notification.setUpdatedAt(Instant.now());
    }
}
