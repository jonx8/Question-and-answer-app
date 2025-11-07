package com.questionanswer.notifications.exception;

public class NotificationAccessDeniedException extends RuntimeException {

    public NotificationAccessDeniedException(String userId, String notificationId) {
        super("User " + userId + " does not have access to notification " + notificationId);
    }

    public NotificationAccessDeniedException(String message) {
        super(message);
    }

    public NotificationAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static NotificationAccessDeniedException withUserAndNotification(String userId, String notificationId) {
        return new NotificationAccessDeniedException(userId, notificationId);
    }
}