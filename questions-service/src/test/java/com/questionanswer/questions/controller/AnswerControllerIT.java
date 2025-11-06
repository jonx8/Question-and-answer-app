package com.questionanswer.questions.controller;

import com.questionanswer.questions.TestConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AnswerControllerIT extends BaseIntegrationTest {
    private static final String PATH_PREFIX = "/api/answers";

    @Test
    @Sql("/sql/answers.sql")
    void getAnswersByAuthor_AuthorHasAnswers_ReturnsAnswers() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .queryParam("author", String.valueOf(TestConstants.USER_ID_1))
                .with(jwt());

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.data[0].id").value(TestConstants.ANSWER_ID_1)
                );
    }

    @Test
    @Sql("/sql/answers.sql")
    void deleteAnswer_UserIsAnswerAuthor_DeletesAnswer() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .delete(PATH_PREFIX + "/" + TestConstants.ANSWER_ID_1)
                .with(jwt()
                        .jwt(jwt -> jwt.subject(String.valueOf(TestConstants.USER_ID_1)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_USER))
                );

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql("/sql/answers.sql")
    void deleteAnswer_UserIsNotAuthorAndNotAdmin_ThrowsAccessDeniedException() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .delete(PATH_PREFIX + "/" + TestConstants.ANSWER_ID_1)
                .with(jwt()
                        .jwt(jwt -> jwt.subject(String.valueOf(TestConstants.USER_ID_2)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_USER))
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isForbidden(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql("/sql/answers.sql")
    void deleteAnswer_UserIsAdmin_DeletesAnswer() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .delete(PATH_PREFIX + "/" + TestConstants.ANSWER_ID_1)
                .with(jwt()
                        .jwt(jwt -> jwt.subject(String.valueOf(TestConstants.ADMIN_USER_ID)))
                        .authorities(new SimpleGrantedAuthority(TestConstants.ROLE_ADMIN))
                );

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }
}