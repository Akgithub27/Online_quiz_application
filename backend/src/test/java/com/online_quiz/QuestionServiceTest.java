package com.online_quiz;

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
import com.online_quiz.service.QuestionService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuestionService questionService;

    private User adminUser;
    private User participantUser;
    private Quiz quiz;
    private Question question;
    private QuestionRequest questionRequest;

    @BeforeEach
    public void setup() {
        // Setup admin user
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(User.UserRole.ADMIN);

        // Setup participant user
        participantUser = new User();
        participantUser.setId(2L);
        participantUser.setEmail("participant@test.com");
        participantUser.setRole(User.UserRole.PARTICIPANT);

        // Setup quiz
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setCreatedBy(adminUser);

        // Setup question
        question = new Question();
        question.setId(1L);
        question.setQuiz(quiz);
        question.setQuestionText("What is 2+2?");
        question.setOptions("[\"2\", \"4\", \"6\", \"8\"]");
        question.setCorrectAnswerIndex(1);
        question.setQuestionOrder(1);

        // Setup question request
        questionRequest = new QuestionRequest();
        questionRequest.setQuizId(1L);
        questionRequest.setQuestionText("What is 2+2?");
        questionRequest.setOptions(Arrays.asList("2", "4", "6", "8"));
        questionRequest.setCorrectAnswerIndex(1);
        questionRequest.setQuestionOrder(1);
    }

    @Test
    public void testCreateQuestion_Success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        QuestionResponse response = questionService.createQuestion(questionRequest, 1L);

        assertNotNull(response);
        assertEquals("What is 2+2?", response.getQuestionText());
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    public void testCreateQuestion_QuizNotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            questionService.createQuestion(questionRequest, 1L);
        });
    }

    @Test
    public void testCreateQuestion_UserNotFound() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            questionService.createQuestion(questionRequest, 99L);
        });
    }

    @Test
    public void testCreateQuestion_UnauthorizedUser() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(2L)).thenReturn(Optional.of(participantUser));

        assertThrows(UnauthorizedException.class, () -> {
            questionService.createQuestion(questionRequest, 2L);
        });
    }

    @Test
    public void testUpdateQuestion_Success() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        QuestionResponse response = questionService.updateQuestion(1L, questionRequest, 1L);

        assertNotNull(response);
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    public void testUpdateQuestion_NotFound() {
        when(questionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            questionService.updateQuestion(99L, questionRequest, 1L);
        });
    }

    @Test
    public void testUpdateQuestion_UnauthorizedUser() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        assertThrows(UnauthorizedException.class, () -> {
            questionService.updateQuestion(1L, questionRequest, 2L);
        });
    }

    @Test
    public void testDeleteQuestion_Success() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        assertDoesNotThrow(() -> {
            questionService.deleteQuestion(1L, 1L);
        });

        verify(questionRepository, times(1)).delete(question);
    }

    @Test
    public void testDeleteQuestion_NotFound() {
        when(questionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            questionService.deleteQuestion(99L, 1L);
        });
    }

    @Test
    public void testGetQuestionsByQuizId_Success() {
        List<Question> questions = Arrays.asList(question);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionRepository.findByQuizOrderByQuestionOrderAsc(quiz)).thenReturn(questions);

        List<QuestionResponse> responses = questionService.getQuestionsByQuizId(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    public void testGetQuestionsByQuizId_QuizNotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            questionService.getQuestionsByQuizId(99L);
        });
    }
}
