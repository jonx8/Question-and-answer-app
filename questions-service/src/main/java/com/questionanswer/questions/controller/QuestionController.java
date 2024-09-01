package com.questionanswer.questions.controller;


import com.questionanswer.questions.controller.dto.AnswerDto;
import com.questionanswer.questions.controller.dto.QuestionDto;
import com.questionanswer.questions.controller.dto.QuestionHeader;
import com.questionanswer.questions.controller.dto.UpdateStatusDto;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@SecurityRequirement(name = "keycloak")
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;


    @GetMapping
    @Operation(summary = "Get questions list", responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = QuestionHeader.class)))
            )}
    )
    public List<QuestionHeader> getQuestions(@RequestParam(required = false) String author,
                                             JwtAuthenticationToken accessToken) {
        return questionService.getQuestions(author, accessToken);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by id", responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "404", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    public Question getQuestion(@PathVariable Long id, JwtAuthenticationToken accessToken) {
        return questionService.getQuestion(id, accessToken);
    }

    @PostMapping
    @Operation(summary = "Create new question", responses = {
            @ApiResponse(responseCode = "201", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "400", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody QuestionDto dto,
                                                   JwtAuthenticationToken accessToken,
                                                   UriComponentsBuilder uriComponentsBuilder) {
        Question question = questionService.createQuestion(dto.title(), dto.text(), accessToken.getName(), dto.status());

        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("/api/questions/{questionId}")
                        .build(Map.of("questionId", question.getId()))
                )
                .body(question);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Get question by id", responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "400", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "404", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    public Question updateQuestion(@PathVariable Long id,
                                   @Valid @RequestBody QuestionDto dto,
                                   JwtAuthenticationToken accessToken) {
        return questionService.updateQuestion(id, dto, accessToken);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Change question status", responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "404", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    public ResponseEntity<Void> changeQuestionStatus(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateStatusDto dto,
                                                     JwtAuthenticationToken accessToken) {
        questionService.changeStatus(id, dto.status(), accessToken);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete question", responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "404", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            ))})
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id, JwtAuthenticationToken accessToken) {
        questionService.deleteQuestion(id, accessToken);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/answer")
    @Operation(summary = "Add answer to the question", responses = {
            @ApiResponse(responseCode = "201", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Question.class)
            )),
            @ApiResponse(responseCode = "400", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "404", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    public ResponseEntity<Question> addAnswer(@PathVariable Long id,
                                              @RequestBody AnswerDto dto,
                                              JwtAuthenticationToken accessToken,
                                              UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("/api/questions/{questionId}")
                        .build(Map.of("questionId", id))
                )
                .body(questionService.addAnswerToQuestion(id, dto.text(), accessToken.getName()));
    }
}
