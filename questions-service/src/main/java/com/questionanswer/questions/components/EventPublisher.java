package com.questionanswer.questions.components;

import com.questionanswer.questions.entity.Answer;

public interface EventPublisher {
    void publishAnswerCreated(Answer answer);
}