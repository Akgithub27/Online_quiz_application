package com.online_quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizWithQuestionsResponse {

    private Long id;
    private String title;
    private String description;
    private Integer timeLimit;
    private List<QuestionResponse> questions;
}
