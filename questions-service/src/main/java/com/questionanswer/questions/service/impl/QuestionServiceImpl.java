package com.questionanswer.questions.service.impl;


import com.questionanswer.questions.controller.DTO.QuestionDTO;
import com.questionanswer.questions.controller.DTO.QuestionHeader;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public Question getQuestion(Long id, JwtAuthenticationToken accessToken) {
        Question question = questionRepository.findById(id).orElseThrow();
        if (!question.getStatus().equals(QuestionStatus.PUBLISHED) && !hasFullAccess(accessToken, question)) {
            throw new AccessDeniedException("You do not have access to this question");
        }
        return question;
    }

    @Override
    public List<QuestionHeader> getQuestions(String authorId, JwtAuthenticationToken accessToken) {
        Predicate<Question> publishedFilter = question -> question.getStatus().equals(QuestionStatus.PUBLISHED);
        Predicate<Question> authorFilter = question -> question.getAuthor().equals(authorId);
        Predicate<Question> filter = publishedFilter;

        if (authorId != null) {
            filter = filter.and(authorFilter);
            if (authorId.equals(accessToken.getName()) || isAdmin(accessToken)) {
                filter = authorFilter;
            }
        }
        return questionRepository.findAll().stream()
                .filter(filter)
                .map(question -> new QuestionHeader(
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
        Question question = new Question(null, title, text, authorId, status, new ArrayList<>(), Timestamp.from(Instant.now()));
        return questionRepository.save(question);
    }


    @Override
    @Transactional
    public Question updateQuestion(Long id, QuestionDTO dto, JwtAuthenticationToken accessToken) {
        Question question = questionRepository.findById(id).orElseThrow();
        if (!hasFullAccess(accessToken, question)) {
            throw new AccessDeniedException("You can not to delete this question");
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
        question.getAnswers().add(new Answer(null, answerText, authorId, question, Timestamp.from(Instant.now())));
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public void changeStatus(Long id, QuestionStatus status, JwtAuthenticationToken accessToken) {
        Question question = questionRepository.findById(id).orElseThrow();
        if (!hasFullAccess(accessToken, question)) {
            throw new AccessDeniedException("You can not to edit this question");
        }
        question.setStatus(status);
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id, JwtAuthenticationToken accessToken) {
        Question question = questionRepository.findById(id).orElseThrow();
        if (!hasFullAccess(accessToken, question)) {
            throw new AccessDeniedException("You can not to delete this question");
        }
        questionRepository.deleteById(id);
    }


    private Boolean hasFullAccess(JwtAuthenticationToken accessToken, Question question) {
        return isAdmin(accessToken) || accessToken.getName().equals(question.getAuthor());
    }

    private Boolean isAdmin(JwtAuthenticationToken accessToken) {
        return accessToken.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
