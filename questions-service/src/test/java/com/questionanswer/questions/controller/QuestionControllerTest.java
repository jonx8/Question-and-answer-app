package com.questionanswer.questions.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    void getQuestionsTest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get(PATH_PREFIX);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(2)
                );

    }

    @Test
    @Sql("/sql/questions.sql")
    void getExistingQuestionWithAnswersTest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get(PATH_PREFIX + "/1");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.title").value("Question title"),
                        jsonPath("$.text").value("Far far away, behind"),
                        jsonPath("$.answers.length()").value(1),
                        jsonPath("$.status").value("PUBLISHED"),
                        jsonPath("$.createdAt").isString()
                );
    }


    @Test
    @Sql("/sql/questions.sql")
    void getNonExistentQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get(PATH_PREFIX + "/111");
        this.mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
    }

    @Test
    void createNewValidQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
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
                        jsonPath("$.answers").isArray(),
                        jsonPath("$.answers").isEmpty(),
                        jsonPath("$.createdAt").isString()
                );
    }

    @Test
    void createNewQuestionWithInvalidStatusField() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":  "New Question", "text": "Something new", "status": "NEW"}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isBadRequest());
    }


    @Test
    void createNewQuestionWithoutTextField() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX)
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
        var requestBuilder = MockMvcRequestBuilders.delete(PATH_PREFIX + "/1");

        this.mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
    }


    @Test
    @Sql("/sql/questions.sql")
    void deleteNonExistentQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.delete(PATH_PREFIX + "/30");

        this.mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
    }


    @Test
    @Sql("/sql/questions.sql")
    void updateExistingQuestionStatus() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.patch(PATH_PREFIX + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status": "BANNED"}""");

        this.mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
    }

    @Test
    void updateNonExistentQuestionStatus() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.patch(PATH_PREFIX + "/1")
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
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"text": "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live."}""");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"title" : "How i met your mother?", "text" :"Lorem ipsum dolor si", "status": "DRAFT"}"""),
                        jsonPath("$.answers.length()").value(1)
                );
    }

    @Test
    void addAnswerToNotExistentQuestion() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post(PATH_PREFIX + "/2/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""" 
                        {"text": "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live."}""");

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isNotFound(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
        );
    }
}