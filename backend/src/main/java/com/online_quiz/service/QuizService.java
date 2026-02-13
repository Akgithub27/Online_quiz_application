package com.online_quiz.service;

import com.online_quiz.dto.QuizRequest;
import com.online_quiz.dto.QuizResponse;
import com.online_quiz.entity.Quiz;
import com.online_quiz.entity.User;
import com.online_quiz.exception.ResourceNotFoundException;
import com.online_quiz.exception.UnauthorizedException;
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
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Transactional
    public QuizResponse createQuiz(QuizRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != User.UserRole.ADMIN) {
            throw new UnauthorizedException("Only admins can create quizzes");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimit(request.getTimeLimit());
        quiz.setIsPublished(request.getIsPublished() != null ? request.getIsPublished() : true);
        quiz.setCreatedBy(user);

        quiz = quizRepository.save(quiz);
        log.info("Quiz created successfully: {}", quiz.getId());

        return QuizResponse.fromEntity(quiz);
    }

    @Transactional
    public QuizResponse updateQuiz(Long quizId, QuizRequest request, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You can only update your own quizzes");
        }

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimit(request.getTimeLimit());
        if (request.getIsPublished() != null) {
            quiz.setIsPublished(request.getIsPublished());
        }

        quiz = quizRepository.save(quiz);
        log.info("Quiz updated successfully: {}", quizId);

        return QuizResponse.fromEntity(quiz);
    }

    @Transactional
    public void deleteQuiz(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own quizzes");
        }

        quizRepository.delete(quiz);
        log.info("Quiz deleted successfully: {}", quizId);
    }

    public QuizResponse getQuizById(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        return QuizResponse.fromEntity(quiz);
    }

    public List<QuizResponse> getPublishedQuizzes() {
        return quizRepository.findByIsPublishedTrue().stream()
                .map(QuizResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<QuizResponse> getAdminQuizzes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return quizRepository.findByCreatedBy(user).stream()
                .map(QuizResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
