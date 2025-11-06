package com.questionanswer.questions.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAnswerRequest(@NotNull @Min(1) Long questionId,
                                  @NotBlank @Size(min = 120, max = 1200) String text) {
}
