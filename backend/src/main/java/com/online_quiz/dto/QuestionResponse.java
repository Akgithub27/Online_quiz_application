package com.online_quiz.dto;

import com.google.gson.Gson;
import com.online_quiz.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private Long id;
    private Long quizId;
    private String questionText;
    private List<String> options;
    private Integer correctAnswerIndex;
    private Integer questionOrder;

    public static QuestionResponse fromEntity(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuizId(question.getQuiz().getId());
        response.setQuestionText(question.getQuestionText());

        // Parse JSON options
        Gson gson = new Gson();
        String[] optionsArray = gson.fromJson(question.getOptions(), String[].class);
        response.setOptions(Arrays.asList(optionsArray));

        response.setCorrectAnswerIndex(question.getCorrectAnswerIndex());
        response.setQuestionOrder(question.getQuestionOrder());
        return response;
    }
}
