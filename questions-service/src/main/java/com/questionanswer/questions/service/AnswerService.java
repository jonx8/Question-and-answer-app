package com.questionanswer.questions.service;


import com.questionanswer.questions.entity.Answer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public interface AnswerService {
    List<Answer> getAnswersByAuthor(String authorId);

    void deleteAnswer(Long id, JwtAuthenticationToken accessToken);
}
