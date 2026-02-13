package com.online_quiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Min(value = 1, message = "Time limit must be at least 1 minute")
    private Integer timeLimit;

    private Boolean isPublished = false;
}
