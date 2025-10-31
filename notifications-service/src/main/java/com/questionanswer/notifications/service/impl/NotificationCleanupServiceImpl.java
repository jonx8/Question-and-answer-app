package com.questionanswer.notifications.service.impl;

import com.questionanswer.notifications.config.NotificationProperties;
import com.questionanswer.notifications.repository.NotificationRepository;
import com.questionanswer.notifications.service.NotificationCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.notifications.cleanup.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationCleanupServiceImpl implements NotificationCleanupService {
    private final NotificationRepository notificationRepository;
    private final NotificationProperties notificationProperties;

    @Override
    @Transactional
    @Scheduled(cron = "#{@notificationProperties.cleanup.cron}")
    public void cleanupOldNotifications() {
        Instant cutoffTime = Instant.now().minus(notificationProperties.getCleanup().getRetentionDays(), ChronoUnit.DAYS);
        long deletedCount = notificationRepository.deleteByCreatedAtBefore(cutoffTime);
        log.info("Deleted {} notifications older than {}", deletedCount, cutoffTime);
    }
}
