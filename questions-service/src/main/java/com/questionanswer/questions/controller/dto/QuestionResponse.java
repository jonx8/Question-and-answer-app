package com.questionanswer.questions.controller.dto;

import com.questionanswer.questions.entity.Answer;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record QuestionResponse(Long id, String title, String text, UUID author, List<Answer> answers,
                               Instant createdAt) {
}
