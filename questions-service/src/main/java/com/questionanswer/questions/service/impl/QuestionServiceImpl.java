package com.questionanswer.questions.service.impl;


import com.questionanswer.questions.controller.dto.QuestionDto;
import com.questionanswer.questions.controller.dto.QuestionHeader;
import com.questionanswer.questions.entity.Answer;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;
import com.questionanswer.questions.repository.QuestionRepository;
import com.questionanswer.questions.service.QuestionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public Question getQuestion(Long id, JwtAuthenticationToken accessToken) {
        Question question = questionRepository.findById(id).orElseThrow();
        if (question.getStatus().equals(QuestionStatus.PUBLISHED) || hasFullAccess(accessToken, question.getAuthor())) {
            return question;
        }
        throw new AccessDeniedException("You do not have access to this question");
    }

    @Override
    public List<QuestionHeader> getQuestions(String authorId, JwtAuthenticationToken accessToken) {
        Stream<Question> questionStream;

        if (authorId != null) {
            questionStream = questionRepository.findAllByAuthorAndStatusOrderByCreatedAtDesc(authorId, QuestionStatus.PUBLISHED).stream();
            if (hasFullAccess(accessToken, authorId)) {
                questionStream = questionRepository.findAllByAuthorOrderByCreatedAtDesc(authorId).stream();
            }
        } else {
            questionStream = questionRepository.findAllByStatusOrderByCreatedAtDesc(QuestionStatus.PUBLISHED).stream();
        }

        return questionStream.map(question -> new QuestionHeader(
                question.getId(),
                question.getTitle(),
                question.getText(),
                question.getAuthor(),
                question.getStatus(),
                question.getCreatedAt()
        )).toList();
    }

    @Override
    @Transactional
    public Question createQuestion(String title, String text, String authorId, QuestionStatus status) {
        Question question = new Question(null, title, text, authorId, status, new ArrayList<>(), Instant.now());
        return questionRepository.save(question);
    }


    @Override
    @Transactional
    public Question updateQuestion(Long id, QuestionDto dto, JwtAuthenticationToken accessToken) {
        Question question = questionRepository.findById(id).orElseThrow();
        if (!hasFullAccess(accessToken, question.getAuthor())) {
            throw new AccessDeniedException("You can not to update this question");
        }
        question.setTitle(dto.title());
        question.setText(dto.text());
        question.setStatus(dto.status());
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question addAnswerToQuestion(Long id, String answerText, String authorId) {
        Question question = questionRepository.findById(id).orElseThrow();
        if (question.getAuthor().equals(authorId)) {
            throw new AccessDeniedException("You can not to add an answer to your own question");
        }
        if (question.getAnswers().stream().anyMatch(answer -> answer.getAuthor().equals(authorId))) {
            throw new AccessDeniedException("You can have the only one answer for each question");
        }
        question.getAnswers().add(new Answer(null, answerText, authorId, question, Instant.now()));
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public void changeStatus(Long id, QuestionStatus status, JwtAuthenticationToken accessToken) {
        Question question = questionRepository.findById(id).orElseThrow();
        if (!hasFullAccess(accessToken, question.getAuthor())) {
            throw new AccessDeniedException("You can not to edit this question");
        }
        question.setStatus(status);
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id, JwtAuthenticationToken accessToken) {
        Question question = questionRepository.findById(id).orElseThrow();
        if (!hasFullAccess(accessToken, question.getAuthor())) {
            throw new AccessDeniedException("You can not to delete this question");
        }
        questionRepository.deleteById(id);
    }


    private boolean hasFullAccess(JwtAuthenticationToken accessToken, String authorId) {
        return isAdmin(accessToken) || accessToken.getName().equals(authorId);
    }

    private boolean isAdmin(JwtAuthenticationToken accessToken) {
        return accessToken.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
