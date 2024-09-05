package com.questionanswer.questions.controller.dto;

import java.time.Instant;

public record AnswerDto(Long id, String text, String author, QuestionHeader question, Instant createdAt) {

}

