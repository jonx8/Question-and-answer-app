package com.questionanswer.questions.controller.DTO;

import com.questionanswer.questions.entity.QuestionStatus;

import java.sql.Timestamp;

public record QuestionHeader(Long id, String title, String text, QuestionStatus status, Timestamp createdAt) {

}
