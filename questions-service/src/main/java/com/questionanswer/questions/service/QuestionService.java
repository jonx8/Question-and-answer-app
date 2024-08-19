package com.questionanswer.questions.service;


import com.questionanswer.questions.controller.DTO.QuestionDTO;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    Question getQuestion(UUID id);

    List<Question> getQuestions();

    Question createQuestion(QuestionDTO dto);

    Question updateQuestion(UUID id, QuestionDTO dto);

    void changeStatus(UUID id, QuestionStatus status);

    void deleteQuestion(UUID id);
}
