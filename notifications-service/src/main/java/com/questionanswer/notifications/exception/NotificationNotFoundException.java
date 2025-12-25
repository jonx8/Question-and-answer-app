package com.questionanswer.notifications.exception;

public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(String id) {
        super("Notification not found with id: " + id);
    }

    public static NotificationNotFoundException withId(String id) {
        return new NotificationNotFoundException(id);
    }

}