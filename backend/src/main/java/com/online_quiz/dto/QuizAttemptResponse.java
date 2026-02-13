package com.online_quiz.dto;

import com.google.gson.Gson;
import com.online_quiz.entity.QuizAttempt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResponse {

    private Long id;
    private Long userId;
    private Long quizId;
    private String quizTitle;
    private Integer score;
    private Integer totalQuestions;
    private Double percentage;
    private Map<String, Integer> selectedAnswers;
    private Integer timeSpent;
    private LocalDateTime submittedAt;

    public static QuizAttemptResponse fromEntity(QuizAttempt attempt) {
        QuizAttemptResponse response = new QuizAttemptResponse();
        response.setId(attempt.getId());
        response.setUserId(attempt.getUser().getId());
        response.setQuizId(attempt.getQuiz().getId());
        response.setQuizTitle(attempt.getQuiz().getTitle());
        response.setScore(attempt.getScore());
        response.setTotalQuestions(attempt.getTotalQuestions());
        response.setPercentage(((double) attempt.getScore() / attempt.getTotalQuestions()) * 100);
        response.setTimeSpent(attempt.getTimeSpent());
        response.setSubmittedAt(attempt.getSubmittedAt());

        // Parse selected answers JSON
        Gson gson = new Gson();
        response.setSelectedAnswers(gson.fromJson(attempt.getSelectedAnswers(), Map.class));

        return response;
    }
}
