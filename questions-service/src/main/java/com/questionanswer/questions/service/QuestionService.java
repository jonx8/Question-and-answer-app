package com.questionanswer.questions.service;


import com.questionanswer.questions.controller.dto.*;
import com.questionanswer.questions.entity.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface QuestionService {

    Question getQuestion(Long id);

    QuestionResponse getQuestionWithAnswers(Long id);

    PagedResponse<QuestionHeader> getQuestions(Pageable pageable);

    PagedResponse<QuestionHeader> getQuestionsByAuthor(UUID authorId, Pageable pageable);

    Question createQuestion(CreateQuestionRequest request, JwtAuthenticationToken accessToken);

    Question updateQuestion(Long id, UpdateQuestionRequest request, JwtAuthenticationToken accessToken);

    void deleteQuestion(Long id, JwtAuthenticationToken accessToken);
}
