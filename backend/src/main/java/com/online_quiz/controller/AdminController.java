package com.online_quiz.controller;

import com.online_quiz.dto.*;
import com.online_quiz.exception.BadRequestException;
import com.online_quiz.exception.ResourceNotFoundException;
import com.online_quiz.service.QuestionService;
import com.online_quiz.service.QuizAttemptService;
import com.online_quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.online_quiz.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management endpoints")
@Slf4j
public class AdminController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final QuizAttemptService quizAttemptService;
    private final UserRepository userRepository;

    // Quiz Endpoints
    @PostMapping("/quiz")
    @Operation(summary = "Create a new quiz", description = "Create a new quiz (Admin only)")
    public ResponseEntity<QuizResponse> createQuiz(
            @Valid @RequestBody QuizRequest request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        QuizResponse response = quizService.createQuiz(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/quiz/{quizId}")
    @Operation(summary = "Update a quiz", description = "Update an existing quiz")
    public ResponseEntity<QuizResponse> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequest request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        QuizResponse response = quizService.updateQuiz(quizId, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/quiz/{quizId}")
    @Operation(summary = "Delete a quiz", description = "Delete a quiz and all its questions")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable Long quizId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        quizService.deleteQuiz(quizId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quiz")
    @Operation(summary = "Get all admin quizzes", description = "Get all quizzes created by the authenticated admin")
    public ResponseEntity<List<QuizResponse>> getAdminQuizzes(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<QuizResponse> quizzes = quizService.getAdminQuizzes(userId);
        return ResponseEntity.ok(quizzes);
    }

    // Question Endpoints
    @PostMapping("/question")
    @Operation(summary = "Create a question", description = "Add a question to a quiz")
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody QuestionRequest request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        QuestionResponse response = questionService.createQuestion(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/question/{questionId}")
    @Operation(summary = "Update a question", description = "Update an existing question")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionRequest request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        QuestionResponse response = questionService.updateQuestion(questionId, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/question/{questionId}")
    @Operation(summary = "Delete a question", description = "Delete a question from a quiz")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long questionId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        questionService.deleteQuestion(questionId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quiz/{quizId}/questions")
    @Operation(summary = "Get questions by quiz", description = "Get all questions for a specific quiz")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByQuiz(@PathVariable Long quizId) {
        List<QuestionResponse> questions = questionService.getQuestionsByQuizId(quizId);
        return ResponseEntity.ok(questions);
    }

    // Results Endpoints
    @GetMapping("/quiz/{quizId}/results")
    @Operation(summary = "Get quiz results", description = "Get all attempts and results for a quiz")
    public ResponseEntity<List<QuizAttemptResponse>> getQuizResults(
            @PathVariable Long quizId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<QuizAttemptResponse> results = quizAttemptService.getQuizResults(quizId, userId);
        return ResponseEntity.ok(results);
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("User authentication failed");
            throw new BadRequestException("User is not authenticated");
        }
        String email = authentication.getName();
        if (email == null || email.trim().isEmpty()) {
            log.warn("Email not found in authentication");
            throw new BadRequestException("Email not found in authentication");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                })
                .getId();
    }
}
