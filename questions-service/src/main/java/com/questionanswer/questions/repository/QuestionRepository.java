package com.questionanswer.questions.repository;

import com.questionanswer.questions.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, ListPagingAndSortingRepository<Question, Long> {
    Page<Question> findAllByAuthorOrderByCreatedAtDesc(UUID author, Pageable pageable);
}
