package com.questionanswer.questions.controller;

import com.questionanswer.questions.TestConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class QuestionControllerIT extends BaseIntegrationTest {
    private static final String PATH_PREFIX = "/api/questions";

    @Test
    @Sql("/sql/questions.sql")
    void getPublishedQuestions_ReturnsPublishedQuestions() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .with(jwt());

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(2),
                        jsonPath("$[0].id").value(TestConstants.QUESTION_ID_1),
                        jsonPath("$[1].id").value(TestConstants.QUESTION_ID_3)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void getQuestionsByAuthor_UserIsAdmin_ReturnsAllUserQuestions() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .queryParam("author", TestConstants.ADMIN_USER_ID)
                .with(jwt().authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(2),
                        jsonPath("$[0].id").value(TestConstants.QUESTION_ID_3),
                        jsonPath("$[1].id").value(TestConstants.QUESTION_ID_4)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void getQuestionsByAuthor_UserIsAuthor_ReturnsOwnQuestions() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .queryParam("author", TestConstants.ADMIN_USER_ID)
                .with(jwt()
                        .jwt(jwt -> jwt.subject(TestConstants.ADMIN_USER_ID))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_USER))
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(2),
                        jsonPath("$[0].id").value(TestConstants.QUESTION_ID_3),
                        jsonPath("$[1].id").value(TestConstants.QUESTION_ID_4)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void getQuestionById_QuestionExistsAndPublished_ReturnsQuestionWithAnswers() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_1)
                .with(jwt()
                        .jwt(jwt -> jwt.subject(TestConstants.ADMIN_USER_ID))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_USER))
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.title").value("Question title"),
                        jsonPath("$.text").value("Far far away, behind"),
                        jsonPath("$.author").value(TestConstants.USER_ID_1),
                        jsonPath("$.answers.length()").value(1),
                        jsonPath("$.status").value("PUBLISHED"),
                        jsonPath("$.createdAt").isString()
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void getQuestionById_QuestionNotFound_ReturnsNotFound() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX + "/" + TestConstants.NON_EXISTENT_QUESTION_ID)
                .with(jwt());

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void createQuestion_ValidData_ReturnsCreatedQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.ADMIN_USER_ID)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":  "New Question", "text": "Something new", "status": "DRAFT"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(1),
                        jsonPath("$.title").value("New Question"),
                        jsonPath("$.status").value("DRAFT"),
                        jsonPath("$.author").value(TestConstants.ADMIN_USER_ID),
                        jsonPath("$.answers").isArray(),
                        jsonPath("$.answers").isEmpty(),
                        jsonPath("$.createdAt").isString()
                );
    }

    @Test
    void createQuestion_InvalidStatus_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.ADMIN_USER_ID)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":  "New Question", "text": "Something new", "status": "NEW"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void createQuestion_MissingText_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.ADMIN_USER_ID)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":  "New Question", "status": "DRAFT"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void updateQuestion_UserIsAdminAndValidData_ReturnsUpdatedQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_2)
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.USER_ID_1))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "How i met your mother?", "text" : "Hello, world!", "status": "PUBLISHED"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"id": 2, "title": "How i met your mother?", "text" : "Hello, world!", "status": "PUBLISHED", answers: []}""")
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void updateQuestion_InvalidTitle_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_2)
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.USER_ID_1))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "How?", "text" : "Hello, world!", "status": "PUBLISHED"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void updateQuestion_QuestionNotFound_ReturnsNotFound() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put(PATH_PREFIX + "/" + TestConstants.NON_EXISTENT_QUESTION_ID)
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.USER_ID_1))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "How i met your mother?", "text" : "Hello, world!", "status": "PUBLISHED"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void deleteQuestion_QuestionExists_ReturnsNoContent() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.delete(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_1)
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.USER_ID_1))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)));

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql("/sql/questions.sql")
    void deleteQuestion_QuestionNotFound_ReturnsNotFound() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.delete(PATH_PREFIX + "/30")
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.USER_ID_1))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void updateQuestionStatus_QuestionExists_ReturnsNoContent() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.patch(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_1)
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.USER_ID_1))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status": "BANNED"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    void updateQuestionStatus_QuestionNotFound_ReturnsNotFound() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.patch(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_1)
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.USER_ID_1))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status": "BANNED"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void addAnswerToQuestion_QuestionExists_ReturnsQuestionWithAnswer() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_2 + "/answer")
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.USER_ID_2)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"text": "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live. Vokalia and Consonantia, there live"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"title" : "How i met your mother?", "text" :"Lorem ipsum dolor si", "status": "DRAFT"}"""),
                        jsonPath("$.answers.length()").value(1),
                        jsonPath("$.answers[0].author").value(TestConstants.USER_ID_2)
                );
    }

    @Test
    void addAnswerToQuestion_QuestionNotFound_ReturnsNotFound() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_2 + "/answer")
                .with(jwt().jwt(jwt -> jwt.subject(TestConstants.USER_ID_2)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(""" 
                        {"text": "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live. Vokalia and Consonantia, there live."}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }
}