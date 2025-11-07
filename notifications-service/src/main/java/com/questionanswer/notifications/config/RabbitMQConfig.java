package com.questionanswer.notifications.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String EXCHANGE_QUESTION_EVENTS = "question-events";
    public static final String QUEUE_NOTIFICATIONS_SERVICE = "notifications-service.queue";
    public static final String ROUTING_KEY_ANSWER_CREATED = "answer.created";

    public static final String DLX_EXCHANGE = "question-events.dlx";
    public static final String DLX_QUEUE = "notification-service.dlx.queue";
    public static final String DLX_ROUTING_KEY = "dead-letter.notification-service";

    @Bean
    public TopicExchange questionEventsExchange() {
        return new TopicExchange(EXCHANGE_QUESTION_EVENTS, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE_NOTIFICATIONS_SERVICE)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(DLX_ROUTING_KEY)
                .ttl(86_400_000)
                .build();
    }

    @Bean
    public Binding answerCreatedBinding(Queue notificationQueue, TopicExchange questionEventsExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(questionEventsExchange)
                .with(ROUTING_KEY_ANSWER_CREATED);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLX_QUEUE)
                .ttl(86_400_000)
                .build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DLX_ROUTING_KEY);
    }
}
