package com.questionanswer.questions.mapper;

import com.questionanswer.questions.controller.dto.PagedResponse;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public class PageMapper {
    public static <T, R> PagedResponse<R> toPagedResponse(Page<T> page, Function<T, R> mapper) {
        return new PagedResponse<>(
                page.stream().map(mapper).toList(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
