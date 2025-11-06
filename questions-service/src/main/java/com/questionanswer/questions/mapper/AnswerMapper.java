package com.questionanswer.questions.mapper;

import com.questionanswer.questions.controller.dto.AnswerResponse;
import com.questionanswer.questions.controller.dto.PagedResponse;
import com.questionanswer.questions.controller.dto.QuestionHeader;
import com.questionanswer.questions.entity.Answer;
import com.questionanswer.questions.entity.Question;

public class AnswerMapper {
    public static AnswerResponse toResponse(Answer answer) {
        Question question = answer.getQuestion();
        return new AnswerResponse(
                answer.getId(),
                answer.getText(),
                answer.getAuthor(),
                new QuestionHeader(
                        question.getId(),
                        question.getTitle(),
                        question.getText(),
                        question.getAuthor(),
                        question.getCreatedAt()
                ),
                answer.getCreatedAt()
        );
    }
}
