package com.questionanswer.questions.service.impl;


import com.questionanswer.questions.controller.DTO.QuestionDTO;
import com.questionanswer.questions.controller.DTO.QuestionHeader;
import com.questionanswer.questions.entity.Answer;
import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;
import com.questionanswer.questions.repository.QuestionRepository;
import com.questionanswer.questions.service.QuestionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public Question getQuestion(Long id) {
        return questionRepository.findById(id).orElseThrow();
    }

    @Override
    public List<QuestionHeader> getQuestions() {
        return questionRepository.findAll().stream()
                .map(question -> new QuestionHeader(
                        question.getId(),
                        question.getTitle(),
                        question.getText(),
                        question.getStatus(),
                        question.getCreatedAt()
                )).toList();
    }

    @Override
    @Transactional
    public Question createQuestion(String title, String text, QuestionStatus status) {
        Question question = new Question(null, title, text, status, new ArrayList<>(), Timestamp.from(Instant.now()));
        return questionRepository.save(question);
    }


    @Override
    @Transactional
    public Question updateQuestion(Long id, QuestionDTO dto) {
        Question question = questionRepository.findById(id).orElseThrow();
        question.setTitle(dto.title());
        question.setText(dto.text());
        question.setStatus(dto.status());
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question addAnswerToQuestion(Long id, String answerText) {
        Question question = questionRepository.findById(id).orElseThrow();
        question.getAnswers().add(new Answer(null, answerText, question, Timestamp.from(Instant.now())));
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public void changeStatus(Long id, QuestionStatus status) {
        Question question = questionRepository.findById(id).orElseThrow();
        question.setStatus(status);
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
