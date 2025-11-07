package com.questionanswer.notifications.exception;

public class NotificationAlreadyReadException extends RuntimeException {

    public NotificationAlreadyReadException(String notificationId) {
        super("Notification " + notificationId + " is already marked as read");
    }

    public static NotificationAlreadyReadException withId(String notificationId) {
        return new NotificationAlreadyReadException(notificationId);
    }
}