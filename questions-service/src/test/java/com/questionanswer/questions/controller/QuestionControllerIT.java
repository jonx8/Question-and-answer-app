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
    void getAllQuestions_ReturnsAllQuestions() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .with(jwt());

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.data.length()").value(4)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void getQuestionsByAuthor_UserIsAdmin_ReturnsAllUserQuestions() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .queryParam("author", String.valueOf(TestConstants.ADMIN_USER_ID))
                .with(jwt().authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.data.length()").value(2)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void getQuestionsByAuthor_UserIsAuthor_ReturnsOwnQuestions() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .queryParam("author", String.valueOf(TestConstants.ADMIN_USER_ID))
                .with(jwt()
                        .jwt(jwt -> jwt.subject(String.valueOf(TestConstants.ADMIN_USER_ID)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_USER))
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.data.length()").value(2)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void getQuestionById_QuestionExistsAndPublished_ReturnsQuestionWithAnswers() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_1)
                .with(jwt()
                        .jwt(jwt -> jwt.subject(String.valueOf(TestConstants.ADMIN_USER_ID)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_USER))
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.title").value("Question title"),
                        jsonPath("$.text").value("Far far away, behind"),
                        jsonPath("$.author").value(TestConstants.USER_ID_1.toString()),
                        jsonPath("$.answers.length()").value(1),
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
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.ADMIN_USER_ID))))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":  "New Question", "text": "Something new"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").exists(),
                        jsonPath("$.title").value("New Question"),
                        jsonPath("$.author").value(TestConstants.ADMIN_USER_ID.toString()),
                        jsonPath("$.answers").isArray(),
                        jsonPath("$.answers").isEmpty(),
                        jsonPath("$.createdAt").isString()
                );
    }

    @Test
    void createQuestion_InvalidData_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.ADMIN_USER_ID))))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "   ", "text": "Something new"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    void createQuestion_MissingText_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.ADMIN_USER_ID))))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":  "New Question"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void updateQuestion_UserIsAdminAndValidData_ReturnsUpdatedQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_2)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.USER_ID_1)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "How i met your mother?", "text": "Hello, world!"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(TestConstants.QUESTION_ID_2),
                        jsonPath("$.title").value("How i met your mother?"),
                        jsonPath("$.text").value("Hello, world!"),
                        jsonPath("$.author").exists()
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void updateQuestion_UserIsAuthor_ReturnsUpdatedQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_1)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.USER_ID_1)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_USER)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Updated Title", "text": "Updated text"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.title").value("Updated Title"),
                        jsonPath("$.text").value("Updated text")
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void updateQuestion_UserNotAuthor_ReturnsForbidden() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_1)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.USER_ID_2)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_USER)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Updated Title", "text": "Updated text"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql("/sql/questions.sql")
    void updateQuestion_InvalidTitle_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_2)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.USER_ID_1)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "How?", "text": "Hello, world!"}""");

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
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.USER_ID_1)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "How i met your mother?", "text": "Hello, world!"}""");

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
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.USER_ID_1)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)));

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql("/sql/questions.sql")
    void deleteQuestion_QuestionNotFound_ReturnsNotFound() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.delete(PATH_PREFIX + "/" + TestConstants.NON_EXISTENT_QUESTION_ID)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.USER_ID_1)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN)));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql("/sql/questions.sql")
    void deleteQuestion_UserNotAuthor_ReturnsForbidden() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.delete(PATH_PREFIX + "/" + TestConstants.QUESTION_ID_1)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(TestConstants.USER_ID_2)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_USER)));

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }
}