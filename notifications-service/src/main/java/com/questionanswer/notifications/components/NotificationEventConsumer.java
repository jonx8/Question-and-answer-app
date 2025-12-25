package com.questionanswer.notifications.components;

import com.questionanswer.notifications.config.RabbitMQConfig;
import com.questionanswer.notifications.entity.Notification;
import com.questionanswer.notifications.events.AnswerCreatedEvent;
import com.questionanswer.notifications.mapper.NotificationMapper;
import com.questionanswer.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICATIONS_SERVICE)
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @RabbitHandler
    public void handleAnswerCreatedEvent(AnswerCreatedEvent event) {
        try {
            Notification notification = notificationMapper.toEntity(event);
            notificationService.createNotification(notification);

        } catch (Exception e) {
            log.error("Failed to process answer event", e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
