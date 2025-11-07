package com.questionanswer.notifications.repository;

import com.questionanswer.notifications.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID receiverId, Pageable pageable);

    List<Notification> findByUserIdAndIsReadFalse(UUID userId);
}
