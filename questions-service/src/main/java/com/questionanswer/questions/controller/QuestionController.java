package com.questionanswer.questions.controller;


import com.questionanswer.questions.controller.DTO.QuestionDTO;
import com.questionanswer.questions.controller.DTO.UpdateStatusDTO;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;


    @GetMapping
    @Operation(summary = "Get questions list", responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = Question.class)))
            )}
    )
    public List<Question> getQuestions() {
        return questionService.getQuestions();
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
    public Question getQuestion(@PathVariable UUID id) {
        return questionService.getQuestion(id);
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
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody QuestionDTO dto,
                                                   UriComponentsBuilder uriComponentsBuilder) {
        Question question = questionService.createQuestion(dto);
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
    public Question updateQuestion(@PathVariable UUID id,
                                   @Valid @RequestBody QuestionDTO dto) {
        return questionService.updateQuestion(id, dto);
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
    public ResponseEntity<Void> changeQuestionStatus(@PathVariable UUID id,
                                                     @Valid @RequestBody UpdateStatusDTO dto) {
        questionService.changeStatus(id, dto.status());
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Delete question", responses = {@ApiResponse(responseCode = "204")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NoSuchElementException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request could not be processed due to incorrect format"
        );
        problemDetail.setProperty("errors", exception.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList()
        );

        return ResponseEntity
                .badRequest()
                .body(problemDetail);
    }
}
