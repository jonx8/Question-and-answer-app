package com.questionanswer.questions.exception;

public class AnswerOwnQuestionException extends RuntimeException {
    public AnswerOwnQuestionException() {
        super("You can not answer your own question");
    }

    public AnswerOwnQuestionException(String message) {
        super(message);
    }

}