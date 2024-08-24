package com.questionanswer.questions.repository;

import com.questionanswer.questions.entity.Question;
import com.questionanswer.questions.entity.QuestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findQuestionsByStatus(QuestionStatus status);
    List<Question> findQuestionsByAuthor(String author);
    List<Question> findQuestionsByAuthorAndStatus(String author, QuestionStatus status);
}
