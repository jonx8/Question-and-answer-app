package com.questionanswer.questions.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class AnswerControllerTest {
    @Autowired
    MockMvc mockMvc;

    final String PATH_PREFIX = "/api/answers";

    @Test
    @Sql("/sql/answers.sql")
    public void getAnswersByAuthorTest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get(PATH_PREFIX)
                .queryParam("author", "e95f8551-8bd3-477b-85b5-a3d4a5c143a8")
                .with(jwt());

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                jsonPath("$[0].id").value(2)
        );
    }


    @Test
    @Sql("/sql/answers.sql")
    public void deleteOwnAnswerBySimpleUser() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .delete(PATH_PREFIX + "/2")
                .with(jwt()
                        .jwt(jwt -> jwt.subject("e95f8551-8bd3-477b-85b5-a3d4a5c143a8"))
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                );

        this.mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
    }


    @Test
    @Sql("/sql/answers.sql")
    public void deleteAnotherUserAnswerBySimpleUser() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .delete(PATH_PREFIX + "/1")
                .with(jwt()
                        .jwt(jwt -> jwt.subject("e95f8551-8bd3-477b-85b5-a3d4a5c143a8"))
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                );

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isForbidden(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
        );
    }


    @Test
    @Sql("/sql/answers.sql")
    public void deleteAnotherUserAnswerByAdminUser() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .delete(PATH_PREFIX + "/1")
                .with(jwt()
                        .jwt(jwt -> jwt.subject("e95f8551-8bd3-477b-85b5-a3d4a5c143a8"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        this.mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
    }
}
