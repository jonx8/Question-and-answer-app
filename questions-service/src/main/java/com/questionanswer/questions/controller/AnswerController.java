package com.questionanswer.questions.controller;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.questionanswer.questions.controller.dto.AnswerDto;
import com.questionanswer.questions.controller.dto.QuestionHeader;
import com.questionanswer.questions.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "keycloak")
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    @GetMapping
    @Operation(summary = "Get answers by author", responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = AnswerDto.class)))
            )
    })
    public List<AnswerDto> getAnswersByAuthor(@RequestParam String author) {
        return answerService.getAnswersByAuthor(author).stream().map(answer -> new AnswerDto(
                answer.getId(),
                answer.getText(),
                answer.getAuthor(),
                new Jackson2ObjectMapperBuilder()
                        .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                        .build().
                        convertValue(answer.getQuestion(), QuestionHeader.class),
                answer.getCreatedAt())
        ).toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete answer", responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "404", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id, JwtAuthenticationToken accessToken) {
        answerService.deleteAnswer(id, accessToken);
        return ResponseEntity.noContent().build();
    }

}
