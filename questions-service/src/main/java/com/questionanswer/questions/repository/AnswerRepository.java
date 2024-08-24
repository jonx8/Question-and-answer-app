package com.questionanswer.questions.repository;

import com.questionanswer.questions.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAnswerByAuthorOrderByCreatedAtDesc(String author);
}
