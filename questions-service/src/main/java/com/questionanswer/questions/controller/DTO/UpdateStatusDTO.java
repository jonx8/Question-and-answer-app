package com.questionanswer.questions.controller.DTO;

import com.questionanswer.questions.entity.QuestionStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusDTO(@NotNull QuestionStatus status) {
}
