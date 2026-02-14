package com.online_quiz.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.online_quiz.dto.deserializer.LongKeyMapDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmitRequest {

    private Long quizId;
    @JsonDeserialize(using = LongKeyMapDeserializer.class)
    private Map<Long, Integer> selectedAnswers; // questionId -> answerIndex
    private Integer timeSpent; // in seconds
}
