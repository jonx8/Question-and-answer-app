package com.questionanswer.questions.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private final String PATH_PREFIX = "/api/questions";


    @Test
    @Sql("/sql/questions.sql")
    void getPublishedQuestionsTest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .with(jwt());
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(2),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[1].id").value(3)
                );

    }

    @Test
    @Sql("/sql/questions.sql")
    void getUserQuestionsWithAdminUserTest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .queryParam("author", "e95f8551-8bd3-477b-85b5-a3d4a5c143a8")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(2),
                        jsonPath("$[0].id").value(3),
                        jsonPath("$[1].id").value(4)
                );

    }


    @Test
    @Sql("/sql/questions.sql")
    void getOwnUserQuestionsUserTest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .queryParam("author", "e95f8551-8bd3-477b-85b5-a3d4a5c143a8")
                .with(jwt()
                        .jwt(jwt -> jwt.subject("e95f8551-8bd3-477b-85b5-a3d4a5c143a8"))
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                );
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(2),
                        jsonPath("$[0].id").value(3),
                        jsonPath("$[1].id").value(4)
                );

    }

    @Test
    @Sql("/sql/questions.sql")
    void getPublishedQuestionWithAnswersTestWithSimpleUser() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX + "/1")
                .with(jwt()
                        .jwt(jwt -> jwt.subject("e95f8551-8bd3-477b-85b5-a3d4a5c143a8"))
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                );
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.title").value("Question title"),
                        jsonPath("$.text").value("Far far away, behind"),
                        jsonPath("$.author").value("9bce5101-38d7-462d-a891-047f6c1b6129"),
                        jsonPath("$.answers.length()").value(1),
                        jsonPath("$.status").value("PUBLISHED"),
                        jsonPath("$.createdAt").isString()
                );
    }


    @Test
    @Sql("/sql/questions.sql")
    void getNonExistentQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX + "/111")
                .with(jwt());
        this.mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
    }

    @Test
    void createNewValidQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
                .with(jwt().jwt(jwt -> jwt.subject("e95f8551-8bd3-477b-85b5-a3d4a5c143a8")))
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
                        jsonPath("$.author").value("e95f8551-8bd3-477b-85b5-a3d4a5c143a8"),
                        jsonPath("$.answers").isArray(),
                        jsonPath("$.answers").isEmpty(),
                        jsonPath("$.createdAt").isString()
                );
    }

    @Test
    void createNewQuestionWithInvalidStatusField() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
                .with(jwt().jwt(jwt -> jwt.subject("e95f8551-8bd3-477b-85b5-a3d4a5c143a8")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":  "New Question", "text": "Something new", "status": "NEW"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isBadRequest());
    }


    @Test
    void createNewQuestionWithoutTextField() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
                .with(jwt().jwt(jwt -> jwt.subject("e95f8551-8bd3-477b-85b5-a3d4a5c143a8")))
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
    void updateExistingQuestionWithCorrectBody() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put(PATH_PREFIX + "/2")
                .with(jwt().jwt(jwt -> jwt.subject("9bce5101-38d7-462d-a891-047f6c1b6129"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
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
    void updateExistingQuestionWithIncorrectTitle() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put(PATH_PREFIX + "/2")
                .with(jwt().jwt(jwt -> jwt.subject("9bce5101-38d7-462d-a891-047f6c1b6129"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
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
    void updateNonExistentQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put(PATH_PREFIX + "/5")
                .with(jwt().jwt(jwt -> jwt.subject("9bce5101-38d7-462d-a891-047f6c1b6129"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
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
    void deleteExistingQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.delete(PATH_PREFIX + "/1")
                .with(jwt().jwt(jwt -> jwt.subject("9bce5101-38d7-462d-a891-047f6c1b6129"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        this.mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
    }


    @Test
    @Sql("/sql/questions.sql")
    void deleteNonExistentQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.delete(PATH_PREFIX + "/30")
                .with(jwt().jwt(jwt -> jwt.subject("9bce5101-38d7-462d-a891-047f6c1b6129"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isNotFound(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
        );
    }


    @Test
    @Sql("/sql/questions.sql")
    void updateExistingQuestionStatus() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.patch(PATH_PREFIX + "/1")
                .with(jwt().jwt(jwt -> jwt.subject("9bce5101-38d7-462d-a891-047f6c1b6129"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status": "BANNED"}""");

        this.mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
    }

    @Test
    void updateNonExistentQuestionStatus() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.patch(PATH_PREFIX + "/1")
                .with(jwt().jwt(jwt -> jwt.subject("9bce5101-38d7-462d-a891-047f6c1b6129"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status": "BANNED"}""");

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isNotFound(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
        );
    }

    @Test
    @Sql("/sql/questions.sql")
    void addAnswerToExistingQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX + "/2/answer")
                .with(jwt().jwt(jwt -> jwt.subject("9660e3c7-0d23-43ff-903d-3ca8296dc2a7")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"text": "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live."}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"title" : "How i met your mother?", "text" :"Lorem ipsum dolor si", "status": "DRAFT"}"""),
                        jsonPath("$.answers.length()").value(1),
                        jsonPath("$.answers[0].author").value("9660e3c7-0d23-43ff-903d-3ca8296dc2a7")
                );
    }

    @Test
    void addAnswerToNotExistentQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX + "/2/answer")
                .with(jwt().jwt(jwt -> jwt.subject("9660e3c7-0d23-43ff-903d-3ca8296dc2a7")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(""" 
                        {"text": "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live."}""");

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isNotFound(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
        );
    }
}