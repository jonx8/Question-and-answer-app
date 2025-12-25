package com.questionanswer.questions.controller;

import com.questionanswer.questions.dto.AnswerResponse;
import com.questionanswer.questions.dto.CreateAnswerRequest;
import com.questionanswer.questions.dto.PagedResponse;
import com.questionanswer.questions.entity.Answer;
import com.questionanswer.questions.service.AnswerService;
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
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    @GetMapping
    @Operation(summary = "Get answers by author")
    @ApiResponse(useReturnTypeSchema = true)
    public PagedResponse<AnswerResponse> getAnswersByAuthor(@RequestParam UUID author,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return answerService.getAnswersByAuthor(author, pageable);
    }

    @PostMapping
    @Operation(summary = "Create an answer to the question")
    @ApiResponse(responseCode = "201", useReturnTypeSchema = true)
    public ResponseEntity<Answer> createAnswerToQuestion(@Valid @RequestBody CreateAnswerRequest dto,
                                                         JwtAuthenticationToken accessToken) {
        Answer answer = answerService.createAnswerWithEvent(dto.questionId(), dto.text(), accessToken);
        return ResponseEntity
                .created(URI.create("/api/questions/%d/answers/%d"
                        .formatted(answer.getQuestion().getId(), answer.getId())))
                .body(answer);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an answer by id")
    @ApiResponse(responseCode = "204", useReturnTypeSchema = true)
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id, JwtAuthenticationToken accessToken) {
        answerService.deleteAnswer(id, accessToken);
        return ResponseEntity.noContent().build();
    }

}
