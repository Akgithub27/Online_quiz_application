package com.online_quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmitRequest {

    private Long quizId;
    private Map<Long, Integer> selectedAnswers; // questionId -> answerIndex
    private Integer timeSpent; // in seconds
}
