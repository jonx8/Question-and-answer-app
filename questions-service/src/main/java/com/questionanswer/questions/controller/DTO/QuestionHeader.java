package com.questionanswer.questions.controller.DTO;

import com.questionanswer.questions.entity.QuestionStatus;

import java.time.Instant;

public record QuestionHeader(Long id, String title, String text, String author, QuestionStatus status, Instant createdAt) {

}
