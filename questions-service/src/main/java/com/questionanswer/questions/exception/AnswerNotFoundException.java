package com.questionanswer.questions.exception;

public class AnswerNotFoundException extends RuntimeException {

    public AnswerNotFoundException(Long id) {
        super("Answer not found with id: " + id);
    }

    public AnswerNotFoundException(String message) {
        super(message);
    }

    public static AnswerNotFoundException withId(Long id) {
        return new AnswerNotFoundException(id);
    }

}