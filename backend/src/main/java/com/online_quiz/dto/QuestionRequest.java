package com.online_quiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotNull(message = "Options are required")
    private List<String> options;

    @Min(value = 0, message = "Correct answer index must be 0 or greater")
    private Integer correctAnswerIndex;

    private Integer questionOrder;
}
