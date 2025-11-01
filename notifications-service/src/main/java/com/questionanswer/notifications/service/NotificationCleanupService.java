package com.questionanswer.notifications.service;

import org.springframework.stereotype.Service;

@Service
public interface NotificationCleanupService {

    void cleanupOldNotifications();
}
