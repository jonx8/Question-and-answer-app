package com.questionanswer.notifications.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AnswerCreatedEvent extends NotificationEvent {
    private Long questionId;
    private String questionTitle;
    private Long answerId;
}
