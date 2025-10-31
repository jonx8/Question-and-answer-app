package com.questionanswer.notifications.controller;

import com.questionanswer.notifications.controller.dto.CreateNotificationRequest;
import com.questionanswer.notifications.controller.dto.NotificationsListResponse;
import com.questionanswer.notifications.controller.dto.UnreadCountResponse;
import com.questionanswer.notifications.controller.dto.UpdateNotificationRequest;
import com.questionanswer.notifications.entity.Notification;
import com.questionanswer.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
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
    public ResponseEntity<Notification> getNotification(@PathVariable UUID id) {
        Notification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @Operation(summary = "Get unread notifications count for user")
    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(@RequestParam UUID userId) {
        long count = notificationService.getUnreadNotificationsCount(userId);
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @Operation(summary = "Create new notification")
    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        Notification notification = notificationService.createNotification(request);
        return ResponseEntity
                .created(URI.create("/api/notifications/%s".formatted(notification.getId().toString())))
                .body(notification);
    }

    @Operation(summary = "Mark notification as read")
    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable UUID id) {

        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @Operation(summary = "Update notification")
    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateNotificationRequest request) {

        Notification notification = notificationService.updateNotification(id, request);
        return ResponseEntity.ok(notification);
    }

    @Operation(summary = "Delete notification by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

}
