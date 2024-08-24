package com.questionanswer.questions.repository;

import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByStatusOrderByCreatedAtDesc(QuestionStatus status);
    List<Question> findAllByAuthorOrderByCreatedAtDesc(String author);
    List<Question> findAllByAuthorAndStatusOrderByCreatedAtDesc(String author, QuestionStatus status);
}
