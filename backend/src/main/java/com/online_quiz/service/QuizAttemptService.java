package com.online_quiz.service;

import com.google.gson.Gson;
import com.online_quiz.dto.QuizSubmitRequest;
import com.online_quiz.dto.QuizAttemptResponse;
import com.online_quiz.entity.Question;
import com.online_quiz.entity.Quiz;
import com.online_quiz.entity.QuizAttempt;
import com.online_quiz.entity.User;
import com.online_quiz.exception.ResourceNotFoundException;
import com.online_quiz.repository.QuestionRepository;
import com.online_quiz.repository.QuizRepository;
import com.online_quiz.repository.QuizAttemptRepository;
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
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final Gson gson = new Gson();

    @Transactional
    public QuizAttemptResponse submitQuiz(QuizSubmitRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        List<Question> questions = questionRepository.findByQuizOrderByQuestionOrderAsc(quiz);

        // Calculate score
        int score = 0;
        for (Question question : questions) {
            if (request.getSelectedAnswers().containsKey(question.getId())) {
                Integer selectedAnswer = request.getSelectedAnswers().get(question.getId());
                if (selectedAnswer.equals(question.getCorrectAnswerIndex())) {
                    score++;
                }
            }
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setScore(score);
        attempt.setTotalQuestions(questions.size());
        attempt.setSelectedAnswers(gson.toJson(request.getSelectedAnswers()));
        attempt.setTimeSpent(request.getTimeSpent());

        attempt = quizAttemptRepository.save(attempt);
        log.info("Quiz submitted successfully - User: {}, Quiz: {}, Score: {}/{}", 
                userId, request.getQuizId(), score, questions.size());

        return QuizAttemptResponse.fromEntity(attempt);
    }

    public List<QuizAttemptResponse> getUserHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return quizAttemptRepository.findByUserOrderBySubmittedAtDesc(user).stream()
                .map(QuizAttemptResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<QuizAttemptResponse> getQuizResults(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(userId)) {
            throw new ResourceNotFoundException("You can only view results for your own quizzes");
        }

        return quizAttemptRepository.findByQuizOrderBySubmittedAtDesc(quiz).stream()
                .map(QuizAttemptResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public QuizAttemptResponse getAttemptDetails(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        return QuizAttemptResponse.fromEntity(attempt);
    }
}
