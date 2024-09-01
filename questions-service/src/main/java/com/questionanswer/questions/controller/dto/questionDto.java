package com.questionanswer.questions.controller.dto;

import com.questionanswer.questions.entity.QuestionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record questionDto(@NotBlank @Size(min = 10, max = 80) String title,
                          @NotBlank @Size(min = 10, max = 300) String text,
                          @NotNull QuestionStatus status) {
}
