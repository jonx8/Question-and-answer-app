package com.questionanswer.questions.mapper;

import com.questionanswer.questions.controller.dto.QuestionHeader;
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
}
