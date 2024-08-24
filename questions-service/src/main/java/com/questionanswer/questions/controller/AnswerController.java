package com.questionanswer.questions.controller;

import com.questionanswer.questions.entity.Answer;
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@SecurityRequirement(name = "keycloak")
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    @GetMapping
    @Operation(summary = "Get answers by author", responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = Answer.class)))
            )
    })
    public List<Answer> getAnswersByAuthor(@RequestParam(required = false) String author) {
        return answerService.getAnswersByAuthor(author);
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
