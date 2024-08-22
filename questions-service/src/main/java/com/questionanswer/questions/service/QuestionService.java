package com.questionanswer.questions.service;


import com.questionanswer.questions.controller.DTO.QuestionDTO;
import com.questionanswer.questions.controller.DTO.QuestionHeader;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;

import java.util.List;

public interface QuestionService {
    Question getQuestion(Long id);

    List<QuestionHeader> getQuestions();

    Question createQuestion(String title, String text, String authorId, QuestionStatus status);

    Question updateQuestion(Long id, QuestionDTO dto);

    Question addAnswerToQuestion(Long id, String answerText, String authorId);

    void changeStatus(Long id, QuestionStatus status);

    void deleteQuestion(Long id);
}
