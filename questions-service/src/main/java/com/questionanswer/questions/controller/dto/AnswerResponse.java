package com.questionanswer.questions.controller.dto;

import java.time.Instant;
import java.util.UUID;

public record AnswerResponse(Long id, String text, UUID author, QuestionHeader question, Instant createdAt) {

}

