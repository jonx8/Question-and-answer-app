package com.questionanswer.questions.service.impl;

import com.questionanswer.questions.TestConstants;
import com.questionanswer.questions.components.SecurityUtils;
import com.questionanswer.questions.dto.AnswerResponse;
import com.questionanswer.questions.dto.PagedResponse;
import com.questionanswer.questions.entity.Answer;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.exception.AnswerAlreadyExistsException;
import com.questionanswer.questions.exception.AnswerNotFoundException;
import com.questionanswer.questions.exception.AnswerOwnQuestionException;
import com.questionanswer.questions.repository.AnswerRepository;
import com.questionanswer.questions.service.QuestionService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AnswerServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
class AnswerServiceImplTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionService questionService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private AnswerServiceImpl answerService;

    private Answer testAnswer;
    private Question testQuestion;
    private JwtAuthenticationToken adminToken;
    private JwtAuthenticationToken answerAuthorToken;
    private JwtAuthenticationToken otherUserToken;
    private JwtAuthenticationToken questionAuthorToken;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testQuestion = new Question(
                TestConstants.QUESTION_ID_1,
                TestConstants.TEST_QUESTION_TITLE,
                TestConstants.TEST_QUESTION_TEXT,
                TestConstants.USER_ID_2,
                new ArrayList<>(),
                Instant.now()
        );

        testAnswer = new Answer(
                TestConstants.ANSWER_ID_1,
                TestConstants.TEST_ANSWER_TEXT,
                TestConstants.USER_ID_1,
                testQuestion,
                Instant.now()
        );

        adminToken = createJwtToken(TestConstants.ADMIN_USER_ID, TestConstants.ROLE_ADMIN);
        answerAuthorToken = createJwtToken(TestConstants.USER_ID_1, TestConstants.ROLE_USER);
        otherUserToken = createJwtToken(TestConstants.USER_ID_2, TestConstants.ROLE_USER);
        questionAuthorToken = createJwtToken(TestConstants.USER_ID_2, TestConstants.ROLE_USER);

        testPageable = PageRequest.of(0, 10);
    }

    @Test
    void getAnswersByAuthor_AuthorHasAnswers_ReturnsPagedResponse() {
        // Arrange
        Page<Answer> answerPage = new PageImpl<>(List.of(testAnswer), testPageable, 1);
        when(answerRepository.findAllByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1, testPageable))
                .thenReturn(answerPage);

        // Act
        PagedResponse<AnswerResponse> result = answerService.getAnswersByAuthor(TestConstants.USER_ID_1, testPageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(1);
        verify(answerRepository).findAllByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1, testPageable);
    }

    @Test
    void getAnswersByAuthor_AuthorHasNoAnswers_ReturnsEmptyPagedResponse() {
        // Arrange
        Page<Answer> emptyPage = new PageImpl<>(List.of(), testPageable, 0);
        when(answerRepository.findAllByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1, testPageable))
                .thenReturn(emptyPage);

        // Act
        PagedResponse<AnswerResponse> result = answerService.getAnswersByAuthor(TestConstants.USER_ID_1, testPageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.data()).isEmpty();
        verify(answerRepository).findAllByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1, testPageable);
    }

    @Test
    void createAnswerToQuestion_ValidData_CreatesAnswer() {
        // Arrange
        when(questionService.getQuestion(TestConstants.QUESTION_ID_1)).thenReturn(testQuestion);
        when(securityUtils.getCurrentUserId(otherUserToken)).thenReturn(TestConstants.USER_ID_1);
        when(answerRepository.existsByQuestionIdAndAuthor(TestConstants.QUESTION_ID_1, TestConstants.USER_ID_1))
                .thenReturn(false);
        when(answerRepository.save(any(Answer.class))).thenReturn(testAnswer);

        // Act
        Answer result = answerService.createAnswerToQuestion(
                TestConstants.QUESTION_ID_1,
                TestConstants.TEST_ANSWER_TEXT,
                otherUserToken
        );

        // Assert
        assertThat(result).isEqualTo(testAnswer);
        verify(questionService).getQuestion(TestConstants.QUESTION_ID_1);
        verify(answerRepository).existsByQuestionIdAndAuthor(TestConstants.QUESTION_ID_1, TestConstants.USER_ID_1);
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    void createAnswerToQuestion_UserAnswersOwnQuestion_ThrowsAnswerOwnQuestionException() {
        // Arrange
        when(questionService.getQuestion(TestConstants.QUESTION_ID_1)).thenReturn(testQuestion);
        when(securityUtils.getCurrentUserId(questionAuthorToken)).thenReturn(TestConstants.USER_ID_2);

        // Act & Assert
        assertThatThrownBy(() -> answerService.createAnswerToQuestion(
                TestConstants.QUESTION_ID_1,
                TestConstants.TEST_ANSWER_TEXT,
                questionAuthorToken
        ))
                .isInstanceOf(AnswerOwnQuestionException.class)
                .hasMessage("You can not answer to your own question");

        verify(answerRepository, never()).save(any(Answer.class));
    }

    @Test
    void createAnswerToQuestion_UserAlreadyAnswered_ThrowsAnswerAlreadyExistsException() {
        // Arrange
        when(questionService.getQuestion(TestConstants.QUESTION_ID_1)).thenReturn(testQuestion);
        when(securityUtils.getCurrentUserId(otherUserToken)).thenReturn(TestConstants.USER_ID_1);
        when(answerRepository.existsByQuestionIdAndAuthor(TestConstants.QUESTION_ID_1, TestConstants.USER_ID_1))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> answerService.createAnswerToQuestion(
                TestConstants.QUESTION_ID_1,
                TestConstants.TEST_ANSWER_TEXT,
                otherUserToken
        ))
                .isInstanceOf(AnswerAlreadyExistsException.class);

        verify(answerRepository, never()).save(any(Answer.class));
    }

    @Test
    void deleteAnswer_UserIsAdmin_DeletesAnswer() {
        // Arrange
        when(answerRepository.findById(TestConstants.ANSWER_ID_1))
                .thenReturn(Optional.of(testAnswer));
        when(securityUtils.isOwnerOrAdmin(adminToken, testAnswer.getAuthor())).thenReturn(true);

        // Act
        answerService.deleteAnswer(TestConstants.ANSWER_ID_1, adminToken);

        // Assert
        verify(answerRepository).delete(testAnswer);
    }

    @Test
    void deleteAnswer_UserIsAnswerAuthor_DeletesAnswer() {
        // Arrange
        when(answerRepository.findById(TestConstants.ANSWER_ID_1))
                .thenReturn(Optional.of(testAnswer));
        when(securityUtils.isOwnerOrAdmin(answerAuthorToken, testAnswer.getAuthor())).thenReturn(true);

        // Act
        answerService.deleteAnswer(TestConstants.ANSWER_ID_1, answerAuthorToken);

        // Assert
        verify(answerRepository).delete(testAnswer);
    }

    @Test
    void deleteAnswer_UserIsNotAuthorAndNotAdmin_ThrowsAccessDeniedException() {
        // Arrange
        when(answerRepository.findById(TestConstants.ANSWER_ID_1))
                .thenReturn(Optional.of(testAnswer));
        when(securityUtils.isOwnerOrAdmin(otherUserToken, testAnswer.getAuthor())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> answerService.deleteAnswer(TestConstants.ANSWER_ID_1, otherUserToken))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have access to this answer");

        verify(answerRepository, never()).delete(any(Answer.class));
    }

    @Test
    void deleteAnswer_AnswerNotFound_ThrowsAnswerNotFoundException() {
        // Arrange
        when(answerRepository.findById(TestConstants.NON_EXISTENT_ANSWER_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> answerService.deleteAnswer(TestConstants.NON_EXISTENT_ANSWER_ID, answerAuthorToken))
                .isInstanceOf(AnswerNotFoundException.class);

        verify(answerRepository, never()).delete(any(Answer.class));
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