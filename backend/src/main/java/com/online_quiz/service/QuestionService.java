package com.online_quiz.service;

import com.google.gson.Gson;
import com.online_quiz.dto.QuestionRequest;
import com.online_quiz.dto.QuestionResponse;
import com.online_quiz.entity.Question;
import com.online_quiz.entity.Quiz;
import com.online_quiz.entity.User;
import com.online_quiz.exception.ResourceNotFoundException;
import com.online_quiz.exception.UnauthorizedException;
import com.online_quiz.repository.QuestionRepository;
import com.online_quiz.repository.QuizRepository;
import com.online_quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final Gson gson = new Gson();

    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request, Long userId) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!quiz.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You can only add questions to your own quizzes");
        }

        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestionText(request.getQuestionText());
        question.setOptions(gson.toJson(request.getOptions()));
        question.setCorrectAnswerIndex(request.getCorrectAnswerIndex());
        question.setQuestionOrder(request.getQuestionOrder());
        question.setIsActive(true);

        question = questionRepository.save(question);
        log.info("Question created successfully: {}", question.getId());

        return QuestionResponse.fromEntity(question);
    }

    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!question.getQuiz().getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You can only update questions in your own quizzes");
        }

        question.setQuestionText(request.getQuestionText());
        question.setOptions(gson.toJson(request.getOptions()));
        question.setCorrectAnswerIndex(request.getCorrectAnswerIndex());
        question.setQuestionOrder(request.getQuestionOrder());

        question = questionRepository.save(question);
        log.info("Question updated successfully: {}", questionId);

        return QuestionResponse.fromEntity(question);
    }

    @Transactional
    public void deleteQuestion(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!question.getQuiz().getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You can only delete questions from your own quizzes");
        }

        questionRepository.delete(question);
        log.info("Question deleted successfully: {}", questionId);
    }

    public List<QuestionResponse> getQuestionsByQuizId(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        return questionRepository.findByQuizOrderByQuestionOrderAsc(quiz).stream()
                .map(QuestionResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
