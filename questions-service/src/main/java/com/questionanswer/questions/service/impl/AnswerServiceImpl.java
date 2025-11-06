package com.questionanswer.questions.service.impl;

import com.questionanswer.questions.components.SecurityUtils;
import com.questionanswer.questions.controller.dto.AnswerResponse;
import com.questionanswer.questions.controller.dto.PagedResponse;
import com.questionanswer.questions.entity.Answer;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.exception.AnswerAlreadyExistsException;
import com.questionanswer.questions.exception.AnswerNotFoundException;
import com.questionanswer.questions.exception.AnswerOwnQuestionException;
import com.questionanswer.questions.exception.QuestionNotFoundException;
import com.questionanswer.questions.mapper.AnswerMapper;
import com.questionanswer.questions.mapper.PageMapper;
import com.questionanswer.questions.repository.AnswerRepository;
import com.questionanswer.questions.service.AnswerService;
import com.questionanswer.questions.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service implementation for managing answers to questions.
 * Provides functionality for creating, retrieving, and deleting answers
 * with proper authorization checks and validation.
 *
 * @author Andrei Malykh <andrei.malykh.0@gmail.com>
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionService questionService;
    private final SecurityUtils securityUtils;

    /**
     * Retrieves a paginated list of answers authored by a specific user.
     * Answers are ordered by creation date in descending order.
     *
     * @param authorId the UUID of the author whose answers to retrieve
     * @param pageable pagination information (page number, size, sorting)
     * @return {@link PagedResponse} containing {@link AnswerResponse} objects
     */
    @Override
    public PagedResponse<AnswerResponse> getAnswersByAuthor(UUID authorId, Pageable pageable) {
        Page<Answer> page = answerRepository.findAllByAuthorOrderByCreatedAtDesc(authorId, pageable);
        return PageMapper.toPagedResponse(page, AnswerMapper::toResponse);
    }

    /**
     * Creates a new answer for the specified question.
     * Validates that the user is not answering their own question and
     * that the user hasn't already answered this question.
     *
     * @param id the ID of the question to answer
     * @param answerText the content of the answer
     * @param accessToken JWT authentication token containing user information
     * @return the created {@link Answer} entity
     * @throws AnswerOwnQuestionException if user attempts to answer their own question
     * @throws AnswerAlreadyExistsException if user has already answered this question
     * @throws QuestionNotFoundException if the specified question doesn't exist
     */
    @Transactional
    @Override
    public Answer createAnswerToQuestion(Long id, String answerText, JwtAuthenticationToken accessToken) {
        Question question = questionService.getQuestion(id);
        UUID userId = securityUtils.getCurrentUserId(accessToken);

        if (question.getAuthor().equals(userId)) {
            log.warn("User {} attempted to answer their own question {}", userId, id);
            throw new AnswerOwnQuestionException("You can not answer to your own question");
        }

        log.debug("Checking if user {} has already answered question {}", userId, id);
        if (answerRepository.existsByQuestionIdAndAuthor(id, userId)) {
            log.warn("User {} attempted to answer question {} multiple times", userId, id);
            throw AnswerAlreadyExistsException.withId(id);
        }

        Answer answer = Answer.builder()
                .text(answerText)
                .question(question)
                .author(UUID.fromString(accessToken.getName()))
                .createdAt(Instant.now())
                .build();

        return answerRepository.save(answer);
    }

    /**
     * Deletes an answer by its ID after verifying user authorization.
     * Only the answer author or an administrator can delete an answer.
     *
     * @param id the ID of the answer to delete
     * @param accessToken JWT authentication token containing user information
     * @throws AnswerNotFoundException if no answer exists with the specified ID
     * @throws AccessDeniedException if user lacks permission to delete the answer
     */
    @Override
    @Transactional
    public void deleteAnswer(Long id, JwtAuthenticationToken accessToken) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Answer with ID: {} not found for deletion", id);
                    return AnswerNotFoundException.withId(id);
                });

        log.debug("Checking delete permissions for user {} on answer {}", accessToken.getName(), id);
        if (!securityUtils.isOwnerOrAdmin(accessToken, answer.getAuthor())) {
            log.warn("Access denied for user {} attempting to delete answer {}", accessToken.getName(), id);
            throw new AccessDeniedException("You do not have access to this answer");
        }

        answerRepository.delete(answer);
    }
}