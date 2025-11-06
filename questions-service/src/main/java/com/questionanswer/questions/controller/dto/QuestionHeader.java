package com.questionanswer.questions.controller.dto;

import java.time.Instant;
import java.util.UUID;

public record QuestionHeader(Long id, String title, String text, UUID author, Instant createdAt) {

}
