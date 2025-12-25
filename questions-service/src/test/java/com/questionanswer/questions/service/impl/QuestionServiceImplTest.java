package com.questionanswer.questions.service.impl;

import com.questionanswer.questions.components.SecurityUtils;
import com.questionanswer.questions.dto.*;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.exception.QuestionNotFoundException;
import com.questionanswer.questions.repository.QuestionRepository;
import com.questionanswer.questions.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link QuestionServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Question testQuestion;
    private JwtAuthenticationToken adminToken;
    private JwtAuthenticationToken questionAuthorToken;
    private JwtAuthenticationToken otherUserToken;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testQuestion = new Question();
        testQuestion.setId(TestConstants.QUESTION_ID_1);
        testQuestion.setTitle(TestConstants.TEST_QUESTION_TITLE);
        testQuestion.setText(TestConstants.TEST_QUESTION_TEXT);
        testQuestion.setAuthor(TestConstants.USER_ID_1);
        testQuestion.setCreatedAt(Instant.now());

        adminToken = createJwtToken(TestConstants.ADMIN_USER_ID, TestConstants.ROLE_ADMIN);
        questionAuthorToken = createJwtToken(TestConstants.USER_ID_1, TestConstants.ROLE_USER);
        otherUserToken = createJwtToken(TestConstants.USER_ID_2, TestConstants.ROLE_USER);

        testPageable = PageRequest.of(0, 10);
    }

    @Test
    void getQuestion_QuestionExists_ReturnsQuestion() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act
        QuestionResponse result = questionService.getQuestionWithAnswers(TestConstants.QUESTION_ID_1);

        // Assert
        assertThat(result.id()).isEqualTo(TestConstants.QUESTION_ID_1);
        assertThat(result.answers()).isEmpty();
        assertThat(result.title()).isEqualTo(TestConstants.TEST_QUESTION_TITLE);
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
    }

    @Test
    void getQuestion_QuestionNotFound_ThrowsQuestionNotFoundException() {
        // Arrange
        when(questionRepository.findById(TestConstants.NON_EXISTENT_QUESTION_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> questionService.getQuestionWithAnswers(TestConstants.NON_EXISTENT_QUESTION_ID))
                .isInstanceOf(QuestionNotFoundException.class);
    }

    @Test
    void getQuestions_ReturnsPagedResponse() {
        // Arrange
        Page<Question> questionPage = new PageImpl<>(List.of(testQuestion), testPageable, 1);
        when(questionRepository.findAll(testPageable)).thenReturn(questionPage);

        // Act
        PagedResponse<QuestionHeader> result = questionService.getQuestions(testPageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(1);
        verify(questionRepository).findAll(testPageable);
    }

    @Test
    void getQuestionsByAuthor_ReturnsPagedResponse() {
        // Arrange
        Page<Question> questionPage = new PageImpl<>(List.of(testQuestion), testPageable, 1);
        when(questionRepository.findAllByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1, testPageable))
                .thenReturn(questionPage);

        // Act
        PagedResponse<QuestionHeader> result = questionService.getQuestionsByAuthor(TestConstants.USER_ID_1, testPageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(1);
        verify(questionRepository).findAllByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1, testPageable);
    }

    @Test
    void createQuestion_ValidData_CreatesAndReturnsQuestion() {
        // Arrange
        CreateQuestionRequest request = new CreateQuestionRequest(
                TestConstants.TEST_QUESTION_TITLE,
                TestConstants.TEST_QUESTION_TEXT
        );
        when(securityUtils.getCurrentUserId(questionAuthorToken)).thenReturn(TestConstants.USER_ID_1);
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        // Act
        Question result = questionService.createQuestion(request, questionAuthorToken);

        // Assert
        assertThat(result).isEqualTo(testQuestion);
        verify(securityUtils).getCurrentUserId(questionAuthorToken);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    void updateQuestion_UserIsAuthor_UpdatesQuestion() {
        // Arrange
        UpdateQuestionRequest request = new UpdateQuestionRequest(
                "Updated Title",
                "Updated Text"
        );
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));
        when(securityUtils.isOwnerOrAdmin(questionAuthorToken, TestConstants.USER_ID_1))
                .thenReturn(true);

        // Act
        Question result = questionService.updateQuestion(TestConstants.QUESTION_ID_1, request, questionAuthorToken);

        // Assert
        assertThat(result).isEqualTo(testQuestion);
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getText()).isEqualTo("Updated Text");
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
        verify(securityUtils).isOwnerOrAdmin(questionAuthorToken, TestConstants.USER_ID_1);
    }

    @Test
    void updateQuestion_UserIsAdmin_UpdatesQuestion() {
        // Arrange
        UpdateQuestionRequest request = new UpdateQuestionRequest(
                "Updated Title",
                "Updated Text"
        );
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));
        when(securityUtils.isOwnerOrAdmin(adminToken, TestConstants.USER_ID_1))
                .thenReturn(true);

        // Act
        Question result = questionService.updateQuestion(TestConstants.QUESTION_ID_1, request, adminToken);

        // Assert
        assertThat(result).isEqualTo(testQuestion);
        verify(securityUtils).isOwnerOrAdmin(adminToken, TestConstants.USER_ID_1);
    }

    @Test
    void updateQuestion_UserIsNotAuthorOrAdmin_ThrowsAccessDeniedException() {
        // Arrange
        UpdateQuestionRequest request = new UpdateQuestionRequest(
                "Updated Title",
                "Updated Text"
        );
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));
        when(securityUtils.isOwnerOrAdmin(otherUserToken, TestConstants.USER_ID_1))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> questionService.updateQuestion(TestConstants.QUESTION_ID_1, request, otherUserToken))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can not to update this question");

        verify(securityUtils).isOwnerOrAdmin(otherUserToken, TestConstants.USER_ID_1);
    }

    @Test
    void deleteQuestion_UserIsAuthor_DeletesQuestion() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));
        when(securityUtils.isOwnerOrAdmin(questionAuthorToken, TestConstants.USER_ID_1))
                .thenReturn(true);

        // Act
        questionService.deleteQuestion(TestConstants.QUESTION_ID_1, questionAuthorToken);

        // Assert
        verify(questionRepository).delete(testQuestion);
        verify(securityUtils).isOwnerOrAdmin(questionAuthorToken, TestConstants.USER_ID_1);
    }

    @Test
    void deleteQuestion_UserIsAdmin_DeletesQuestion() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));
        when(securityUtils.isOwnerOrAdmin(adminToken, TestConstants.USER_ID_1))
                .thenReturn(true);

        // Act
        questionService.deleteQuestion(TestConstants.QUESTION_ID_1, adminToken);

        // Assert
        verify(questionRepository).delete(testQuestion);
        verify(securityUtils).isOwnerOrAdmin(adminToken, TestConstants.USER_ID_1);
    }

    @Test
    void deleteQuestion_UserIsNotAuthorOrAdmin_ThrowsAccessDeniedException() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));
        when(securityUtils.isOwnerOrAdmin(otherUserToken, TestConstants.USER_ID_1))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> questionService.deleteQuestion(TestConstants.QUESTION_ID_1, otherUserToken))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can not to delete this question");

        verify(questionRepository, never()).delete(any(Question.class));
        verify(securityUtils).isOwnerOrAdmin(otherUserToken, TestConstants.USER_ID_1);
    }

    @Test
    void deleteQuestion_QuestionNotFound_ThrowsQuestionNotFoundException() {
        // Arrange
        when(questionRepository.findById(TestConstants.NON_EXISTENT_QUESTION_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> questionService.deleteQuestion(TestConstants.NON_EXISTENT_QUESTION_ID, questionAuthorToken))
                .isInstanceOf(QuestionNotFoundException.class);

        verify(questionRepository, never()).delete(any(Question.class));
    }

    private JwtAuthenticationToken createJwtToken(UUID subject, String... authorities) {
        List<SimpleGrantedAuthority> grantedAuthorities = Stream.of(authorities)
                .map(SimpleGrantedAuthority::new)
                .toList();

        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .claim("sub", subject.toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        return new JwtAuthenticationToken(jwt, grantedAuthorities, subject.toString());
    }
}