package com.questionanswer.questions.mapper;

import com.questionanswer.questions.dto.QuestionHeader;
import com.questionanswer.questions.dto.QuestionResponse;
import com.questionanswer.questions.entity.Question;


public class QuestionMapper {
    public static QuestionHeader toHeader(Question question) {
        return new QuestionHeader(
                question.getId(),
                question.getTitle(),
                question.getText().substring(0, Math.min(question.getText().length(), 120)) + "...",
                question.getAuthor(),
                question.getCreatedAt()
        );
    }

    public static QuestionResponse toResponse(Question question) {
        return new QuestionResponse(question.getId(), question.getTitle(), question.getText(), question.getAuthor(), question.getAnswers(), question.getCreatedAt());
    }
}
