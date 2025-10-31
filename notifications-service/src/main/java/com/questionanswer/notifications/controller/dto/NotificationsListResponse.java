package com.questionanswer.notifications.controller.dto;

import com.questionanswer.notifications.entity.Notification;

import java.util.List;

public record NotificationsListResponse(
        List<Notification> data,
        int currentPage,
        int totalPages,
        long totalItems,
        boolean hasNext,
        boolean hasPrevious
) {
}
