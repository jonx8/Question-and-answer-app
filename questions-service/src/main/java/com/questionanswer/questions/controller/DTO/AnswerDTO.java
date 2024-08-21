package com.questionanswer.questions.controller.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerDTO(@NotBlank @Size(min = 120, max = 1200) String text) {
}
