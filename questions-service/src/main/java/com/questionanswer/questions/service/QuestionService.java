package com.questionanswer.questions.service;


import com.questionanswer.questions.controller.DTO.CreateQuestionDTO;
import com.questionanswer.questions.controller.DTO.UpdateQuestionDTO;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    Question getQuestion(UUID id);

    List<Question> getQuestions();

    Question createQuestion(CreateQuestionDTO dto);

    Question updateQuestion(UUID id, UpdateQuestionDTO dto);

    void changeStatus(UUID id, QuestionStatus status);

    void deleteQuestion(UUID id);
}
