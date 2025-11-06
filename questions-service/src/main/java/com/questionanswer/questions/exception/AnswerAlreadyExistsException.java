package com.questionanswer.questions.exception;

public class AnswerAlreadyExistsException extends RuntimeException {

    public AnswerAlreadyExistsException(Long questionId) {
        super("Answer already exists for question with id: " + questionId);
    }

    public AnswerAlreadyExistsException(String message) {
        super(message);
    }

    public static AnswerAlreadyExistsException withId(Long questionId) {
        return new AnswerAlreadyExistsException(questionId);
    }
}