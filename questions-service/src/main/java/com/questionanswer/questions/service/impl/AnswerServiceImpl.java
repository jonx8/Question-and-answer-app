package com.questionanswer.questions.service.impl;


import com.questionanswer.questions.entity.Answer;
import com.questionanswer.questions.repository.AnswerRepository;
import com.questionanswer.questions.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;


    @Override
    public List<Answer> getAnswersByAuthor(String authorId) {
        return answerRepository.findAnswerByAuthorOrderByCreatedAtDesc(authorId);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long id, JwtAuthenticationToken accessToken) {
        if (accessToken.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            answerRepository.deleteById(id);
            return;
        }

        Answer answer = answerRepository.findById(id).orElseThrow();

        if (accessToken.getName().equals(answer.getAuthor())) {
            answerRepository.delete(answer);
        } else {
            throw new AccessDeniedException("You do not have access to this answer");
        }
    }
}
