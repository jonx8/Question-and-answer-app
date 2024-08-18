package com.questionanswer.questions.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "questions")
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    String text;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    QuestionStatus status;

    @CreationTimestamp
    @Column(nullable = false)
    Timestamp createdAt;


}
