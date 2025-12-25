package com.questionanswer.notifications.controller;

import com.questionanswer.notifications.dto.NotificationsListResponse;
import com.questionanswer.notifications.entity.Notification;
import com.questionanswer.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "keycloak")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Get user notifications", description = "Retrieves paginated notifications for a specific user")
    @GetMapping
    public ResponseEntity<NotificationsListResponse> getUserNotifications(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        NotificationsListResponse notifications = notificationService.getNotificationsByUserId(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Get unread notifications for user")
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@RequestParam UUID userId) {
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(unreadNotifications);
    }

    @Operation(summary = "Get notification by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable String id) {
        Notification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @Operation(summary = "Mark notification as read")
    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable String id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @Operation(summary = "Delete notification by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

}
