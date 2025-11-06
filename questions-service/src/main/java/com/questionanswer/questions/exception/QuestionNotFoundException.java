package com.questionanswer.questions.exception;

public class QuestionNotFoundException extends RuntimeException {

    public QuestionNotFoundException(Long id) {
        super("Question not found with id: " + id);
    }

    public QuestionNotFoundException(String message) {
        super(message);
    }

    public static QuestionNotFoundException withId(Long id) {
        return new QuestionNotFoundException(id);
    }

}