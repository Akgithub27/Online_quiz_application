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
    private Quiz quiz;

    @BeforeEach
    public void setup() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(User.UserRole.ADMIN);

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Java Basics");
        quiz.setDescription("Learn Java basics");
        quiz.setTimeLimit(30);
        quiz.setCreatedBy(adminUser);
    }

    @Test
    public void testCreateQuizSuccess() {
        QuizRequest request = new QuizRequest();
        request.setTitle("Java Basics");
        request.setDescription("Learn Java basics");
        request.setTimeLimit(30);

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        QuizResponse response = quizService.createQuiz(request, 1L);

        assertNotNull(response);
        assertEquals("Java Basics", response.getTitle());
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    public void testCreateQuizUserNotAdmin() {
        User participantUser = new User();
        participantUser.setId(2L);
        participantUser.setRole(User.UserRole.PARTICIPANT);

        QuizRequest request = new QuizRequest();
        request.setTitle("Java Basics");

        when(userRepository.findById(2L)).thenReturn(Optional.of(participantUser));

        assertThrows(UnauthorizedException.class, () -> {
            quizService.createQuiz(request, 2L);
        });
    }

    @Test
    public void testDeleteQuizSuccess() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        assertDoesNotThrow(() -> quizService.deleteQuiz(1L, 1L));
        verify(quizRepository, times(1)).delete(quiz);
    }

    @Test
    public void testDeleteQuizNotFound() {
        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            quizService.deleteQuiz(999L, 1L);
        });
    }
}
