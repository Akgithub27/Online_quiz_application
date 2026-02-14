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
public class ParticipantQuestionResponse {

    private Long id;
    private String questionText;
    private List<String> options;
    private Integer questionOrder;

    public static ParticipantQuestionResponse fromEntity(Question question) {
        ParticipantQuestionResponse response = new ParticipantQuestionResponse();
        response.setId(question.getId());
        response.setQuestionText(question.getQuestionText());

        // Parse JSON options
        Gson gson = new Gson();
        String[] optionsArray = gson.fromJson(question.getOptions(), String[].class);
        response.setOptions(Arrays.asList(optionsArray));

        // DO NOT include correctAnswerIndex for participants
        response.setQuestionOrder(question.getQuestionOrder());
        return response;
    }
}
