package com.questionanswer.questions.service.impl;

import com.questionanswer.questions.controller.dto.QuestionDto;
import com.questionanswer.questions.controller.dto.QuestionHeader;
import com.questionanswer.questions.entity.Answer;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;
import com.questionanswer.questions.repository.QuestionRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Question testQuestion;
    private JwtAuthenticationToken adminToken;
    private JwtAuthenticationToken userToken;
    private JwtAuthenticationToken otherUserToken;

    @BeforeEach
    void setUp() {
        testQuestion = new Question(
                TestConstants.QUESTION_ID_1,
                TestConstants.TEST_QUESTION_TITLE,
                TestConstants.TEST_QUESTION_TEXT,
                TestConstants.USER_ID_1,
                QuestionStatus.PUBLISHED,
                new ArrayList<>(),
                Instant.now()
        );

        adminToken = createJwtToken(TestConstants.ADMIN_USER_ID, TestConstants.ROLE_ADMIN);
        userToken = createJwtToken(TestConstants.USER_ID_1, TestConstants.ROLE_USER);
        otherUserToken = createJwtToken(TestConstants.USER_ID_2, TestConstants.ROLE_USER);
    }

    @Test
    void getQuestion_QuestionExistsAndUserIsAuthor_ReturnsQuestion() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act
        Question result = questionService.getQuestion(TestConstants.QUESTION_ID_1, userToken);

        // Assert
        assertThat(result.getId()).isEqualTo(TestConstants.QUESTION_ID_1);
        assertThat(result.getTitle()).isEqualTo(TestConstants.TEST_QUESTION_TITLE);
        assertThat(result.getAuthor()).isEqualTo(userToken.getToken().getSubject());
        assertThat(result.getAnswers()).isEmpty();
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
    }

    @Test
    void getQuestion_QuestionExistsAndUserIsAdmin_ReturnsQuestion() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act
        Question result = questionService.getQuestion(TestConstants.QUESTION_ID_1, adminToken);

        // Assert
        assertThat(result.getId()).isEqualTo(TestConstants.QUESTION_ID_1);
        assertThat(result.getTitle()).isEqualTo(TestConstants.TEST_QUESTION_TITLE);
        assertThat(result.getAuthor()).isEqualTo(userToken.getToken().getSubject());
        assertThat(result.getAnswers()).isEmpty();
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
    }

    @Test
    void getQuestion_QuestionNotFound_ThrowsException() {
        // Arrange
        when(questionRepository.findById(TestConstants.NON_EXISTENT_QUESTION_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> questionService.getQuestion(TestConstants.NON_EXISTENT_QUESTION_ID, userToken))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getQuestion_QuestionIsDraftAndUserIsNotAuthor_ThrowsAccessDeniedException() {
        // Arrange
        testQuestion.setStatus(QuestionStatus.DRAFT);
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act & Assert
        assertThatThrownBy(() -> questionService.getQuestion(TestConstants.QUESTION_ID_1, otherUserToken))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have access to this question");
    }

    @Test
    void getQuestion_QuestionIsDraftAndUserIsAuthor_ReturnsQuestion() {
        // Arrange
        testQuestion.setStatus(QuestionStatus.DRAFT);
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act
        Question result = questionService.getQuestion(TestConstants.QUESTION_ID_1, userToken);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TestConstants.QUESTION_ID_1);
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
    }

    @Test
    void getQuestion_QuestionIsDraftAndUserIsAdmin_ReturnsQuestion() {
        // Arrange
        testQuestion.setStatus(QuestionStatus.DRAFT);
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act
        Question result = questionService.getQuestion(TestConstants.QUESTION_ID_1, adminToken);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TestConstants.QUESTION_ID_1);
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
    }

    @Test
    void getQuestions_NoAuthorId_ReturnsPublishedQuestions() {
        // Arrange
        List<Question> publishedQuestions = List.of(testQuestion);
        when(questionRepository.findAllByStatusOrderByCreatedAtDesc(QuestionStatus.PUBLISHED))
                .thenReturn(publishedQuestions);

        // Act
        List<QuestionHeader> result = questionService.getQuestions(null, userToken);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(TestConstants.QUESTION_ID_1);
        assertThat(result.get(0).title()).isEqualTo(TestConstants.TEST_QUESTION_TITLE);
        verify(questionRepository).findAllByStatusOrderByCreatedAtDesc(QuestionStatus.PUBLISHED);
    }

    @Test
    void getQuestions_AuthorIdProvidedAndUserIsNotAuthorOrAdmin_ReturnsOnlyPublishedQuestions() {
        // Arrange
        List<Question> publishedQuestions = List.of(testQuestion);
        when(questionRepository.findAllByAuthorAndStatusOrderByCreatedAtDesc(TestConstants.USER_ID_1, QuestionStatus.PUBLISHED))
                .thenReturn(publishedQuestions);

        // Act
        List<QuestionHeader> result = questionService.getQuestions(TestConstants.USER_ID_1, otherUserToken);

        // Assert
        assertThat(result).hasSize(1);
        verify(questionRepository).findAllByAuthorAndStatusOrderByCreatedAtDesc(TestConstants.USER_ID_1, QuestionStatus.PUBLISHED);
        verify(questionRepository, never()).findAllByAuthorOrderByCreatedAtDesc(any());
    }

    @Test
    void getQuestions_AuthorIdProvidedAndUserIsAuthor_ReturnsAllQuestions() {
        // Arrange
        List<Question> allQuestions = List.of(testQuestion);
        when(questionRepository.findAllByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1))
                .thenReturn(allQuestions);

        // Act
        List<QuestionHeader> result = questionService.getQuestions(TestConstants.USER_ID_1, userToken);

        // Assert
        assertThat(result).hasSize(1);
        verify(questionRepository).findAllByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1);
    }

    @Test
    void getQuestions_AuthorIdProvidedAndUserIsAdmin_ReturnsAllQuestions() {
        // Arrange
        List<Question> allQuestions = List.of(testQuestion);
        when(questionRepository.findAllByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1))
                .thenReturn(allQuestions);

        // Act
        List<QuestionHeader> result = questionService.getQuestions(TestConstants.USER_ID_1, adminToken);

        // Assert
        assertThat(result).hasSize(1);
        verify(questionRepository).findAllByAuthorOrderByCreatedAtDesc(TestConstants.USER_ID_1);
    }

    @Test
    void createQuestion_ValidData_CreatesAndReturnsQuestion() {
        // Arrange
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        // Act
        Question result = questionService.createQuestion(
                TestConstants.TEST_QUESTION_TITLE,
                TestConstants.TEST_QUESTION_TEXT,
                TestConstants.USER_ID_1,
                QuestionStatus.PUBLISHED
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(TestConstants.TEST_QUESTION_TITLE);
        assertThat(result.getText()).isEqualTo(TestConstants.TEST_QUESTION_TEXT);
        assertThat(result.getAuthor()).isEqualTo(TestConstants.USER_ID_1);
        assertThat(result.getStatus()).isEqualTo(QuestionStatus.PUBLISHED);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    void updateQuestion_UserIsAuthor_UpdatesQuestion() {
        // Arrange
        QuestionDto dto = new QuestionDto("Updated Title", "Updated Text", QuestionStatus.DRAFT);
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        // Act
        Question result = questionService.updateQuestion(TestConstants.QUESTION_ID_1, dto, userToken);

        // Assert
        assertThat(result).isNotNull();
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    void updateQuestion_UserIsNotAuthor_ThrowsAccessDeniedException() {
        // Arrange
        QuestionDto dto = new QuestionDto("Updated Title", "Updated Text", QuestionStatus.DRAFT);
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act & Assert
        assertThatThrownBy(() -> questionService.updateQuestion(TestConstants.QUESTION_ID_1, dto, otherUserToken))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can not to update this question");
    }

    @Test
    void addAnswerToQuestion_QuestionExistsAndUserIsNotAuthor_AddsAnswer() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        // Act
        Question result = questionService.addAnswerToQuestion(
                TestConstants.QUESTION_ID_1,
                TestConstants.TEST_ANSWER_TEXT,
                TestConstants.USER_ID_2
        );

        // Assert
        assertThat(result).isNotNull();
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    void addAnswerToQuestion_UserIsAuthor_ThrowsAccessDeniedException() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act & Assert
        assertThatThrownBy(() -> questionService.addAnswerToQuestion(
                TestConstants.QUESTION_ID_1,
                TestConstants.TEST_ANSWER_TEXT,
                TestConstants.USER_ID_1
        )).isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can not to add an answer to your own question");
    }

    @Test
    void addAnswerToQuestion_UserAlreadyAnswered_ThrowsAccessDeniedException() {
        // Arrange
        Answer existingAnswer = new Answer(null, "Existing answer", TestConstants.USER_ID_2, testQuestion, Instant.now());
        testQuestion.getAnswers().add(existingAnswer);
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act & Assert
        assertThatThrownBy(() -> questionService.addAnswerToQuestion(
                TestConstants.QUESTION_ID_1,
                TestConstants.TEST_ANSWER_TEXT,
                TestConstants.USER_ID_2
        )).isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can have the only one answer for each question");
    }

    @Test
    void changeStatus_UserIsAuthor_ChangesStatus() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        // Act
        questionService.changeStatus(TestConstants.QUESTION_ID_1, QuestionStatus.BANNED, userToken);

        // Assert
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    void changeStatus_UserIsNotAuthor_ThrowsAccessDeniedException() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act & Assert
        assertThatThrownBy(() -> questionService.changeStatus(TestConstants.QUESTION_ID_1, QuestionStatus.BANNED, otherUserToken))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can not to edit this question");
    }

    @Test
    void deleteQuestion_UserIsAuthor_DeletesQuestion() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act
        questionService.deleteQuestion(TestConstants.QUESTION_ID_1, userToken);

        // Assert
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
        verify(questionRepository).deleteById(TestConstants.QUESTION_ID_1);
    }

    @Test
    void deleteQuestion_UserIsNotAuthor_ThrowsAccessDeniedException() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act & Assert
        assertThatThrownBy(() -> questionService.deleteQuestion(TestConstants.QUESTION_ID_1, otherUserToken))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can not to delete this question");
    }

    @Test
    void deleteQuestion_UserIsAdmin_DeletesQuestion() {
        // Arrange
        when(questionRepository.findById(TestConstants.QUESTION_ID_1))
                .thenReturn(Optional.of(testQuestion));

        // Act
        questionService.deleteQuestion(TestConstants.QUESTION_ID_1, adminToken);

        // Assert
        verify(questionRepository).findById(TestConstants.QUESTION_ID_1);
        verify(questionRepository).deleteById(TestConstants.QUESTION_ID_1);
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