package com.questionanswer.questions.service;


import com.questionanswer.questions.controller.dto.QuestionDto;
import com.questionanswer.questions.controller.dto.QuestionHeader;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public interface QuestionService {
    Question getQuestion(Long id, JwtAuthenticationToken accessToken);

    List<QuestionHeader> getQuestions(String authorId, JwtAuthenticationToken accessToken);

    Question createQuestion(String title, String text, String authorId, QuestionStatus status);

    Question updateQuestion(Long id, QuestionDto dto, JwtAuthenticationToken accessToken);

    Question addAnswerToQuestion(Long id, String answerText, String authorId);

    void changeStatus(Long id, QuestionStatus status, JwtAuthenticationToken accessToken);

    void deleteQuestion(Long id, JwtAuthenticationToken accessToken);
}
