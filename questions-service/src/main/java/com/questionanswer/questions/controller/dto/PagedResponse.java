package com.questionanswer.questions.controller.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> data,
        int currentPage,
        int totalPages,
        long totalItems,
        boolean hasNext,
        boolean hasPrevious
) {
}
