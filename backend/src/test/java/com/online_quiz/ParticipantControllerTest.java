package com.online_quiz;

import com.online_quiz.entity.Quiz;
import com.online_quiz.entity.User;
import com.online_quiz.repository.QuizRepository;
import com.online_quiz.repository.QuizAttemptRepository;
import com.online_quiz.repository.UserRepository;
import com.online_quiz.service.QuizAttemptService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuizAttemptService quizAttemptService;

    @MockBean
    private QuizRepository quizRepository;

    @MockBean
    private QuizAttemptRepository quizAttemptRepository;

    @MockBean
    private UserRepository userRepository;

    private User participantUser;
    private User adminUser;
    private Quiz quiz;

    @BeforeEach
    public void setup() {
        participantUser = new User();
        participantUser.setId(1L);
        participantUser.setEmail("participant@test.com");
        participantUser.setRole(User.UserRole.PARTICIPANT);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(User.UserRole.ADMIN);

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Description");
        quiz.setTimeLimit(60);
        quiz.setIsPublished(true);
        quiz.setCreatedBy(adminUser);
        quiz.setQuestions(new HashSet<>());
    }

    @Test
    public void testGetPublishedQuizzes() throws Exception {
        when(quizRepository.findByIsPublishedTrue()).thenReturn(Arrays.asList(quiz));

        mockMvc.perform(get("/api/quizzes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetQuizDetails() throws Exception {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        mockMvc.perform(get("/api/quiz/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetQuizDetails_NotFound() throws Exception {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/quiz/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSubmitQuiz_Success() throws Exception {
        String submitRequest = "{\"quizId\": 1, \"selectedAnswers\": {\"1\": 0, \"2\": 1}, \"timeSpent\": 300}";

        mockMvc.perform(post("/api/quiz/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(submitRequest))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetUserHistory() throws Exception {
        when(quizAttemptService.getUserHistory(anyLong())).thenReturn(new java.util.ArrayList<>());

        mockMvc.perform(get("/api/user/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAttemptDetails() throws Exception {
        mockMvc.perform(get("/api/attempt/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
