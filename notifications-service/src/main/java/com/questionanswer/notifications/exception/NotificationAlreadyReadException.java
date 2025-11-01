package com.questionanswer.notifications.exception;

import java.util.UUID;

public class NotificationAlreadyReadException extends RuntimeException {

    public NotificationAlreadyReadException(UUID notificationId) {
        super("Notification " + notificationId + " is already marked as read");
    }

    public NotificationAlreadyReadException(String message) {
        super(message);
    }

    public static NotificationAlreadyReadException forId(UUID notificationId) {
        return new NotificationAlreadyReadException(notificationId);
    }
}