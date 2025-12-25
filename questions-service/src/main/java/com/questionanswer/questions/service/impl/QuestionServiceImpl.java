package com.questionanswer.questions.service.impl;

import com.questionanswer.questions.components.SecurityUtils;
import com.questionanswer.questions.dto.*;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.exception.QuestionNotFoundException;
import com.questionanswer.questions.mapper.PageMapper;
import com.questionanswer.questions.mapper.QuestionMapper;
import com.questionanswer.questions.repository.QuestionRepository;
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
import java.util.ArrayList;
import java.util.UUID;

/**
 * Service implementation for managing questions.
 * Provides functionality for creating, retrieving, updating, and deleting questions
 * with proper authorization checks and validation.
 *
 * @author Andrei Malykh <andrei.malykh.0@gmail.com>
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final SecurityUtils securityUtils;


    /**
     * Retrieves a question by its ID without initialize LazyCollection of answer.
     *
     * @param id the ID of the question to retrieve
     * @return the {@link Question} entity
     * @throws QuestionNotFoundException if no question exists with the specified ID
     */
    @Override
    public Question getQuestion(Long id) {
        return questionRepository.findById(id).orElseThrow(() -> {
            log.warn("Question with ID: {} not found", id);
            return QuestionNotFoundException.withId(id);
        });
    }

    /**
     * Retrieves a question by its ID with all answers.
     *
     * @param id the ID of the question to retrieve
     * @return the {@link QuestionResponse} response
     * @throws QuestionNotFoundException if no question exists with the specified ID
     */
    @Override
    public QuestionResponse getQuestionWithAnswers(Long id) {
        Question question = getQuestion(id);
        return QuestionMapper.toResponse(question);
    }


    /**
     * Retrieves a paginated list of all questions.
     *
     * @param pageable pagination information (page number, size, sorting)
     * @return {@link PagedResponse} containing {@link QuestionHeader} objects
     */
    @Override
    public PagedResponse<QuestionHeader> getQuestions(Pageable pageable) {
        Page<Question> page = questionRepository.findAll(pageable);
        return PageMapper.toPagedResponse(page, QuestionMapper::toHeader);
    }

    /**
     * Retrieves a paginated list of questions authored by a specific user.
     * Questions are ordered by creation date in descending order.
     *
     * @param authorId the UUID of the author whose questions to retrieve
     * @param pageable pagination information (page number, size, sorting)
     * @return {@link PagedResponse} containing {@link QuestionHeader} objects
     */
    @Override
    public PagedResponse<QuestionHeader> getQuestionsByAuthor(UUID authorId, Pageable pageable) {

        Page<Question> page = questionRepository.findAllByAuthorOrderByCreatedAtDesc(authorId, pageable);
        return PageMapper.toPagedResponse(page, QuestionMapper::toHeader);
    }

    /**
     * Creates a new question.
     *
     * @param request the question creation data
     * @param accessToken JWT authentication token containing user information
     * @return the created {@link Question} entity
     */
    @Transactional
    @Override
    public Question createQuestion(CreateQuestionRequest request, JwtAuthenticationToken accessToken) {
        UUID userId = securityUtils.getCurrentUserId(accessToken);

        Question question = new Question();
        question.setTitle(request.title());
        question.setText(request.text());
        question.setAuthor(userId);
        question.setAnswers(new ArrayList<>());
        question.setCreatedAt(Instant.now());

        return questionRepository.save(question);
    }

    /**
     * Updates an existing question.
     * Only the question author or an administrator can update the question.
     *
     * @param id the ID of the question to update
     * @param request the question update data
     * @param accessToken JWT authentication token containing user information
     * @return the updated {@link Question} entity
     * @throws QuestionNotFoundException if no question exists with the specified ID
     * @throws AccessDeniedException if user lacks permission to update the question
     */
    @Transactional
    @Override
    public Question updateQuestion(Long id, UpdateQuestionRequest request, JwtAuthenticationToken accessToken) {
        Question question = getQuestion(id);

        if (!securityUtils.isOwnerOrAdmin(accessToken, question.getAuthor())) {
            log.warn("Access denied for user {} attempting to update question {}", accessToken.getName(), id);
            throw new AccessDeniedException("You can not to update this question");
        }

        question.setTitle(request.title());
        question.setText(request.text());

        return question;
    }

    /**
     * Deletes a question by its ID after verifying user authorization.
     * Only the question author or an administrator can delete the question.
     *
     * @param id the ID of the question to delete
     * @param accessToken JWT authentication token containing user information
     * @throws QuestionNotFoundException if no question exists with the specified ID
     * @throws AccessDeniedException if user lacks permission to delete the question
     */
    @Transactional
    @Override
    public void deleteQuestion(Long id, JwtAuthenticationToken accessToken) {
        Question question = getQuestion(id);

        if (!securityUtils.isOwnerOrAdmin(accessToken, question.getAuthor())) {
            log.warn("Access denied for user {} attempting to delete question {}", accessToken.getName(), id);
            throw new AccessDeniedException("You can not to delete this question");
        }

        questionRepository.delete(question);
    }
}