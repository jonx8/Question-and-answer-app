package com.questionanswer.questions.service.impl;


import com.questionanswer.questions.controller.DTO.CreateQuestionDTO;
import com.questionanswer.questions.controller.DTO.UpdateQuestionDTO;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;
import com.questionanswer.questions.repository.QuestionRepository;
import com.questionanswer.questions.service.QuestionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public Question getQuestion(UUID id) {
        return questionRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Question> getQuestions() {
        return questionRepository.findAll();
    }

    @Override
    @Transactional
    public Question createQuestion(CreateQuestionDTO dto) {
        Question question = new Question(null, dto.title(), dto.text(), dto.status(), null);
        return questionRepository.save(question);
    }


    @Override
    @Transactional
    public Question updateQuestion(UUID id, UpdateQuestionDTO dto) {
        Question question = questionRepository.findById(id).orElseThrow();
        question.setTitle(dto.title());
        question.setText(dto.text());
        question.setStatus(dto.status());
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public void changeStatus(UUID id, QuestionStatus status) {
        Question question = questionRepository.findById(id).orElseThrow();
        question.setStatus(status);
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(UUID id) {
        questionRepository.deleteById(id);
    }
}
