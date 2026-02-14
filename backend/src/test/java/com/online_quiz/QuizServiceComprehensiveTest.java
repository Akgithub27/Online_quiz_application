package com.online_quiz;

import com.online_quiz.dto.QuizRequest;
import com.online_quiz.dto.QuizResponse;
import com.online_quiz.entity.Quiz;
import com.online_quiz.entity.User;
import com.online_quiz.exception.ResourceNotFoundException;
import com.online_quiz.exception.UnauthorizedException;
import com.online_quiz.repository.QuizRepository;
import com.online_quiz.repository.UserRepository;
import com.online_quiz.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuizService quizService;

    private User adminUser;
    private User participantUser;
    private Quiz quiz;
    private QuizRequest quizRequest;

    @BeforeEach
    public void setup() {
        // Setup admin user
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@test.com");
        adminUser.setName("Admin User");
        adminUser.setRole(User.UserRole.ADMIN);
        adminUser.setIsActive(true);

        // Setup participant user
        participantUser = new User();
        participantUser.setId(2L);
        participantUser.setEmail("participant@test.com");
        participantUser.setName("Participant");
        participantUser.setRole(User.UserRole.PARTICIPANT);
        participantUser.setIsActive(true);

        // Setup quiz
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Description");
        quiz.setTimeLimit(60);
        quiz.setIsPublished(true);
        quiz.setCreatedBy(adminUser);

        // Setup quiz request
        quizRequest = new QuizRequest();
        quizRequest.setTitle("New Quiz");
        quizRequest.setDescription("New Description");
        quizRequest.setTimeLimit(45);
        quizRequest.setIsPublished(true);
    }

    @Test
    public void testCreateQuiz_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        QuizResponse response = quizService.createQuiz(quizRequest, 1L);

        assertNotNull(response);
        assertEquals("Test Quiz", response.getTitle());
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    public void testCreateQuiz_UserNotFound() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            quizService.createQuiz(quizRequest, 3L);
        });
    }

    @Test
    public void testCreateQuiz_NonAdminUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(participantUser));

        assertThrows(UnauthorizedException.class, () -> {
            quizService.createQuiz(quizRequest, 2L);
        });
    }

    @Test
    public void testUpdateQuiz_Success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        QuizResponse response = quizService.updateQuiz(1L, quizRequest, 1L);

        assertNotNull(response);
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    public void testUpdateQuiz_QuizNotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            quizService.updateQuiz(99L, quizRequest, 1L);
        });
    }

    @Test
    public void testUpdateQuiz_UnauthorizedUser() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        assertThrows(UnauthorizedException.class, () -> {
            quizService.updateQuiz(1L, quizRequest, 2L);
        });
    }

    @Test
    public void testDeleteQuiz_Success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        assertDoesNotThrow(() -> {
            quizService.deleteQuiz(1L, 1L);
        });

        verify(quizRepository, times(1)).delete(quiz);
    }

    @Test
    public void testDeleteQuiz_QuizNotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            quizService.deleteQuiz(99L, 1L);
        });
    }

    @Test
    public void testDeleteQuiz_UnauthorizedUser() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        assertThrows(UnauthorizedException.class, () -> {
            quizService.deleteQuiz(1L, 2L);
        });
    }

    @Test
    public void testGetQuizById_Success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        QuizResponse response = quizService.getQuizById(1L);

        assertNotNull(response);
        assertEquals("Test Quiz", response.getTitle());
    }

    @Test
    public void testGetQuizById_NotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            quizService.getQuizById(99L);
        });
    }

    @Test
    public void testGetPublishedQuizzes() {
        List<Quiz> quizzes = Arrays.asList(quiz);
        when(quizRepository.findByIsPublishedTrue()).thenReturn(quizzes);

        List<QuizResponse> responses = quizService.getPublishedQuizzes();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    public void testGetAdminQuizzes() {
        List<Quiz> quizzes = Arrays.asList(quiz);
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(quizRepository.findByCreatedByOrderByCreatedAtDesc(adminUser)).thenReturn(quizzes);

        List<QuizResponse> responses = quizService.getAdminQuizzes(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }
}
