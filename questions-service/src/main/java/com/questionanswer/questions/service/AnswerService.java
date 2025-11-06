package com.questionanswer.questions.service;


import com.questionanswer.questions.controller.dto.AnswerResponse;
import com.questionanswer.questions.controller.dto.PagedResponse;
import com.questionanswer.questions.entity.Answer;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface AnswerService {
    PagedResponse<AnswerResponse> getAnswersByAuthor(UUID authorId, Pageable pageable);

    Answer createAnswerToQuestion(Long id, String answerText, JwtAuthenticationToken accessToken);

    void deleteAnswer(Long id, JwtAuthenticationToken accessToken);
}
