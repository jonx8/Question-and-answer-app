package com.questionanswer.questions.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AnswerCreatedEvent extends NotificationEvent{
    private Long questionId;
    private String questionTitle;
    private Long answerId;
}
