package com.online_quiz.controller;

import com.online_quiz.dto.*;
import com.online_quiz.entity.Question;
import com.online_quiz.repository.QuestionRepository;
import com.online_quiz.repository.UserRepository;
import com.online_quiz.service.QuestionService;
import com.online_quiz.service.QuizAttemptService;
import com.online_quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Participant", description = "Participant quiz endpoints")
public class ParticipantController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final QuizAttemptService quizAttemptService;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @GetMapping("/quizzes")
    @PreAuthorize("hasRole('PARTICIPANT')")
    @Operation(summary = "Get all published quizzes", description = "Get all available quizzes for participants")
    public ResponseEntity<List<QuizResponse>> getPublishedQuizzes() {
        List<QuizResponse> quizzes = quizService.getPublishedQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasRole('PARTICIPANT')")
    @Operation(summary = "Get quiz details", description = "Get quiz details with questions")
    public ResponseEntity<ParticipantQuizWithQuestionsResponse> getQuizDetails(@PathVariable Long quizId) {
        QuizResponse quiz = quizService.getQuizById(quizId);
        com.online_quiz.entity.Quiz quizEntity = new com.online_quiz.entity.Quiz();
        quizEntity.setId(quizId);
        List<Question> questions = questionRepository.findByQuizOrderByQuestionOrderAsc(quizEntity);

        ParticipantQuizWithQuestionsResponse response = new ParticipantQuizWithQuestionsResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        response.setTimeLimit(quiz.getTimeLimit());
        response.setQuestions(questions.stream()
                .map(ParticipantQuestionResponse::fromEntity)
                .toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/quiz/submit")
    @PreAuthorize("hasRole('PARTICIPANT')")
    @Operation(summary = "Submit quiz", description = "Submit completed quiz and get score")
    public ResponseEntity<QuizAttemptResponse> submitQuiz(
            @Valid @RequestBody QuizSubmitRequest request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        QuizAttemptResponse response = quizAttemptService.submitQuiz(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/history")
    @PreAuthorize("hasRole('PARTICIPANT')")
    @Operation(summary = "Get user quiz history", description = "Get all quiz attempts by the current user")
    public ResponseEntity<List<QuizAttemptResponse>> getUserHistory(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<QuizAttemptResponse> history = quizAttemptService.getUserHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/attempt/{attemptId}")
    @PreAuthorize("hasRole('PARTICIPANT')")
    @Operation(summary = "Get attempt details", description = "Get detailed results of a specific quiz attempt")
    public ResponseEntity<QuizAttemptResponse> getAttemptDetails(@PathVariable Long attemptId) {
        QuizAttemptResponse response = quizAttemptService.getAttemptDetails(attemptId);
        return ResponseEntity.ok(response);
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email))
                .getId();
    }
}
