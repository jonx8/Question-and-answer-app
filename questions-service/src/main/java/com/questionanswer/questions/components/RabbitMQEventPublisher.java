package com.questionanswer.questions.components;

import com.questionanswer.questions.entity.Answer;
import com.questionanswer.questions.entity.NotificationEventType;
import com.questionanswer.questions.events.AnswerCreatedEvent;
import com.questionanswer.questions.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements EventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Async
    @Override
    public void publishAnswerCreated(Answer answer) {
        AnswerCreatedEvent event = new AnswerCreatedEvent();
        event.setEventType(NotificationEventType.ANSWER_CREATED);
        event.setTimestamp(Instant.now());
        event.setActorId(answer.getAuthor());
        event.setUserId(answer.getQuestion().getAuthor());
        event.setQuestionId(answer.getQuestion().getId());
        event.setQuestionTitle(answer.getQuestion().getTitle());
        event.setAnswerId(answer.getId());

        try {
            sendEvent(event, "answer.created");
        } catch (AmqpException e) {
            log.error("Failed to publish ANSWER_CREATED event for answerId: {}", answer.getId(), e);
        }

    }

    private void sendEvent(NotificationEvent event, String routingKey) {
        rabbitTemplate.convertAndSend(
                "question-events",
                routingKey,
                event,
                message -> {
                    String typeId = event.getEventType().name().toLowerCase().replace(".", "_");
                    message.getMessageProperties().setHeader("__TypeId__", typeId);
                    return message;
                }
        );
    }

}
