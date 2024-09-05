package com.questionanswer.questions.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAnswerDto(@NotBlank @Size(min = 120, max = 1200) String text) {
}
