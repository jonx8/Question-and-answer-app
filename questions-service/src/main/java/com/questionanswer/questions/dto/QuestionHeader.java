package com.questionanswer.questions.dto;

import java.time.Instant;
import java.util.UUID;

public record QuestionHeader(Long id, String title, String text, UUID author, Instant createdAt) {

}
