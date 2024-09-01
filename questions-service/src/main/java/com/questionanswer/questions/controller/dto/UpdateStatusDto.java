package com.questionanswer.questions.controller.dto;

import com.questionanswer.questions.entity.QuestionStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusDto(@NotNull QuestionStatus status) {
}
