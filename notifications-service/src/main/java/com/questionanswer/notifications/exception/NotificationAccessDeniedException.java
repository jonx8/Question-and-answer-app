package com.questionanswer.notifications.exception;

import java.util.UUID;

public class NotificationAccessDeniedException extends RuntimeException {

    public NotificationAccessDeniedException(String userId, UUID notificationId) {
        super("User " + userId + " does not have access to notification " + notificationId);
    }

    public NotificationAccessDeniedException(String message) {
        super(message);
    }

    public NotificationAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static NotificationAccessDeniedException forUserAndNotification(String userId, UUID notificationId) {
        return new NotificationAccessDeniedException(userId, notificationId);
    }
}