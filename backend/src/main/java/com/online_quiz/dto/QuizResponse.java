package com.online_quiz.dto;

import com.online_quiz.entity.Quiz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    private Long id;
    private String title;
    private String description;
    private Integer timeLimit;
    private Boolean isPublished;
    private Long createdById;
    private String createdByName;
    private Integer questionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static QuizResponse fromEntity(Quiz quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        response.setTimeLimit(quiz.getTimeLimit());
        response.setIsPublished(quiz.getIsPublished());
        response.setCreatedById(quiz.getCreatedBy().getId());
        response.setCreatedByName(quiz.getCreatedBy().getName());
        response.setQuestionCount(quiz.getQuestions().size());
        response.setCreatedAt(quiz.getCreatedAt());
        response.setUpdatedAt(quiz.getUpdatedAt());
        return response;
    }
}
