package com.online_quiz;

import com.online_quiz.dto.QuizSubmitRequest;
import com.online_quiz.dto.QuizAttemptResponse;
import com.online_quiz.entity.*;
import com.online_quiz.exception.ResourceNotFoundException;
import com.online_quiz.repository.QuestionRepository;
import com.online_quiz.repository.QuizRepository;
import com.online_quiz.repository.QuizAttemptRepository;
import com.online_quiz.repository.UserRepository;
import com.online_quiz.service.QuizAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizAttemptServiceTest {

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuizAttemptService quizAttemptService;

    private User participantUser;
    private User adminUser;
    private Quiz quiz;
    private Question question1;
    private Question question2;
    private QuizAttempt quizAttempt;

    @BeforeEach
    public void setup() {
        // Setup participant user
        participantUser = new User();
        participantUser.setId(1L);
        participantUser.setEmail("participant@test.com");
        participantUser.setName("Participant");
        participantUser.setRole(User.UserRole.PARTICIPANT);

        // Setup admin user
        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(User.UserRole.ADMIN);

        // Setup quiz
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Description");
        quiz.setTimeLimit(60);
        quiz.setCreatedBy(adminUser);

        // Setup questions
        question1 = new Question();
        question1.setId(1L);
        question1.setQuiz(quiz);
        question1.setQuestionText("Question 1");
        question1.setCorrectAnswerIndex(0);
        question1.setQuestionOrder(1);

        question2 = new Question();
        question2.setId(2L);
        question2.setQuiz(quiz);
        question2.setQuestionText("Question 2");
        question2.setCorrectAnswerIndex(1);
        question2.setQuestionOrder(2);

        // Setup quiz attempt
        quizAttempt = new QuizAttempt();
        quizAttempt.setId(1L);
        quizAttempt.setUser(participantUser);
        quizAttempt.setQuiz(quiz);
        quizAttempt.setScore(2);
        quizAttempt.setTotalQuestions(2);
        quizAttempt.setSelectedAnswers("{\"1\": 0, \"2\": 1}");
        quizAttempt.setTimeSpent(300);
    }

    @Test
    public void testSubmitQuiz_Success() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setQuizId(1L);
        request.setSelectedAnswers(new HashMap<Long, Integer>() {{
            put(1L, 0);
            put(2L, 1);
        }});
        request.setTimeSpent(300);

        when(userRepository.findById(1L)).thenReturn(Optional.of(participantUser));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionRepository.findByQuizOrderByQuestionOrderAsc(quiz)).thenReturn(Arrays.asList(question1, question2));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(quizAttempt);

        QuizAttemptResponse response = quizAttemptService.submitQuiz(request, 1L);

        assertNotNull(response);
        assertEquals(2, response.getScore());
        verify(quizAttemptRepository, times(1)).save(any(QuizAttempt.class));
    }

    @Test
    public void testSubmitQuiz_UserNotFound() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setQuizId(1L);
        request.setSelectedAnswers(new HashMap<>());

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            quizAttemptService.submitQuiz(request, 99L);
        });
    }

    @Test
    public void testSubmitQuiz_QuizNotFound() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setQuizId(99L);
        request.setSelectedAnswers(new HashMap<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(participantUser));
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            quizAttemptService.submitQuiz(request, 1L);
        });
    }

    @Test
    public void testGetUserHistory() {
        List<QuizAttempt> attempts = Arrays.asList(quizAttempt);
        when(userRepository.findById(1L)).thenReturn(Optional.of(participantUser));
        when(quizAttemptRepository.findByUserOrderBySubmittedAtDesc(participantUser)).thenReturn(attempts);

        List<QuizAttemptResponse> responses = quizAttemptService.getUserHistory(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    public void testGetAttemptDetails() {
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(quizAttempt));

        QuizAttemptResponse response = quizAttemptService.getAttemptDetails(1L);

        assertNotNull(response);
        assertEquals(2, response.getScore());
    }

    @Test
    public void testGetAttemptDetails_NotFound() {
        when(quizAttemptRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            quizAttemptService.getAttemptDetails(99L);
        });
    }

    @Test
    public void testGetQuizResults() {
        List<QuizAttempt> attempts = Arrays.asList(quizAttempt);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizAttemptRepository.findByQuizOrderBySubmittedAtDesc(quiz)).thenReturn(attempts);

        List<QuizAttemptResponse> responses = quizAttemptService.getQuizResults(1L, 2L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }
}
