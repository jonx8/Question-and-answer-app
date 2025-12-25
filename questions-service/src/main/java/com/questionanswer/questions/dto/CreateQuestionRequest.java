package com.questionanswer.questions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CreateQuestionRequest(
        @NotBlank @Size(min = 10, max = 80) String title,
        @NotBlank @Size(min = 10, max = 300) String text
) {
}
