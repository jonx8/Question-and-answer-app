package com.questionanswer.questions.controller;


import com.questionanswer.questions.controller.dto.*;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "keycloak")
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;


    @GetMapping
    public PagedResponse<QuestionHeader> getQuestions(@RequestParam(required = false) UUID author,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (author == null) {
            return questionService.getQuestions(pageable);
        }
        return questionService.getQuestionsByAuthor(author, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by id")
    public QuestionResponse getQuestion(@PathVariable Long id) {
        return questionService.getQuestionWithAnswers(id);
    }

    @PostMapping
    @Operation(summary = "Create a new question")
    @ApiResponse(responseCode = "201", useReturnTypeSchema = true)
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody CreateQuestionRequest dto, JwtAuthenticationToken accessToken) {
        Question question = questionService.createQuestion(dto, accessToken);

        return ResponseEntity
                .created(URI.create("/api/questions/" + question.getId()))
                .body(question);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Get a question by id")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public Question updateQuestion(@PathVariable Long id,
                                   @Valid @RequestBody UpdateQuestionRequest request,
                                   JwtAuthenticationToken accessToken) {
        return questionService.updateQuestion(id, request, accessToken);
    }


    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", useReturnTypeSchema = true)
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id, JwtAuthenticationToken accessToken) {
        questionService.deleteQuestion(id, accessToken);
        return ResponseEntity.noContent().build();
    }
}
