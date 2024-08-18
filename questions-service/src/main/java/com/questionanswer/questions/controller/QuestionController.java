package com.questionanswer.questions.controller;


import com.questionanswer.questions.controller.DTO.CreateQuestionDTO;
import com.questionanswer.questions.controller.DTO.UpdateQuestionDTO;
import com.questionanswer.questions.controller.DTO.UpdateStatusDTO;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public List<Question> getQuestions() {
        return questionService.getQuestions();
    }

    @GetMapping("/{id}")
    public Question getQuestion(@PathVariable UUID id) {
        return questionService.getQuestion(id);
    }

    @PostMapping
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody CreateQuestionDTO dto,
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
    public Question updateQuestion(@PathVariable UUID id,
                                   @Valid @RequestBody UpdateQuestionDTO dto) {
        return questionService.updateQuestion(id, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> changeQuestionStatus(@PathVariable UUID id,
                                                     @Valid @RequestBody UpdateStatusDTO dto) {
        questionService.changeStatus(id, dto.status());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
