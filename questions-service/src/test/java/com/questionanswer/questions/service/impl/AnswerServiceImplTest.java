package com.questionanswer.questions.service.impl;

import com.questionanswer.questions.entity.Answer;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;
import com.questionanswer.questions.repository.AnswerRepository;
import com.questionanswer.questions.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnswerServiceImplTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerServiceImpl answerService;

    private Answer testAnswer;
    private JwtAuthenticationToken adminToken;
    private JwtAuthenticationToken answerAuthorToken;
    private JwtAuthenticationToken otherUserToken;

    @BeforeEach
    void setUp() {
        Question testQuestion = new Question(
                TestConstants.QUESTION_ID_1,
                TestConstants.TEST_QUESTION_TITLE,
                TestConstants.TEST_QUESTION_TEXT,
                TestConstants.USER_ID_2,
                QuestionStatus.PUBLISHED,
                new java.util.ArrayList<>(),
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
    }

    @Test
    void getAnswersByAuthor_AuthorHasAnswers_ReturnsAnswers() {
        // Arrange
        when(answerRepository.findAnswerByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1))
                .thenReturn(List.of(testAnswer));

        // Act
        List<Answer> result = answerService.getAnswersByAuthor(TestConstants.USER_ID_1);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(TestConstants.ANSWER_ID_1);
        verify(answerRepository).findAnswerByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1);
    }

    @Test
    void getAnswersByAuthor_AuthorHasNoAnswers_ReturnsEmptyList() {
        // Arrange
        when(answerRepository.findAnswerByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1))
                .thenReturn(List.of());

        // Act
        List<Answer> result = answerService.getAnswersByAuthor(TestConstants.USER_ID_1);

        // Assert
        assertThat(result).isEmpty();
        verify(answerRepository).findAnswerByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1);
    }

    @Test
    void deleteAnswer_UserIsAdmin_DeletesAnswerById() {
        // Act
        answerService.deleteAnswer(TestConstants.ANSWER_ID_1, adminToken);

        // Assert
        verify(answerRepository).deleteById(TestConstants.ANSWER_ID_1);
        verify(answerRepository, never()).findById(anyLong());
    }

    @Test
    void deleteAnswer_UserIsAnswerAuthor_DeletesAnswer() {
        // Arrange
        when(answerRepository.findById(TestConstants.ANSWER_ID_1))
                .thenReturn(Optional.of(testAnswer));

        // Act
        answerService.deleteAnswer(TestConstants.ANSWER_ID_1, answerAuthorToken);

        // Assert
        verify(answerRepository).delete(testAnswer);
        verify(answerRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteAnswer_UserIsNotAuthorAndNotAdmin_ThrowsAccessDeniedException() {
        // Arrange
        when(answerRepository.findById(TestConstants.ANSWER_ID_1))
                .thenReturn(Optional.of(testAnswer));

        // Act & Assert
        assertThatThrownBy(() -> answerService.deleteAnswer(TestConstants.ANSWER_ID_1, otherUserToken))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have access to this answer");

        verify(answerRepository, never()).delete(any(Answer.class));
        verify(answerRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteAnswer_AnswerNotFound_ThrowsException() {
        // Arrange
        when(answerRepository.findById(TestConstants.NON_EXISTENT_ANSWER_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> answerService.deleteAnswer(TestConstants.NON_EXISTENT_ANSWER_ID, answerAuthorToken))
                .isInstanceOf(NoSuchElementException.class);

        verify(answerRepository, never()).delete(any(Answer.class));
        verify(answerRepository, never()).deleteById(anyLong());
    }

    private JwtAuthenticationToken createJwtToken(String subject, String... authorities) {
        List<SimpleGrantedAuthority> grantedAuthorities = Stream.of(authorities)
                .map(SimpleGrantedAuthority::new)
                .toList();

        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .claim("sub", subject)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        return new JwtAuthenticationToken(jwt, grantedAuthorities, subject);
    }
}