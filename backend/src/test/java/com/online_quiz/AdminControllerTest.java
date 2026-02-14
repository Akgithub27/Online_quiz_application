package com.online_quiz;

import com.online_quiz.dto.QuizRequest;
import com.online_quiz.entity.Quiz;
import com.online_quiz.entity.User;
import com.online_quiz.exception.ResourceNotFoundException;
import com.online_quiz.exception.UnauthorizedException;
import com.online_quiz.repository.QuizRepository;
import com.online_quiz.repository.UserRepository;
import com.online_quiz.service.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuizService quizService;

    @MockBean
    private QuizRepository quizRepository;

    @MockBean
    private UserRepository userRepository;

    private User adminUser;
    private Quiz quiz;

    @BeforeEach
    public void setup() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(User.UserRole.ADMIN);

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Description");
        quiz.setTimeLimit(60);
        quiz.setCreatedBy(adminUser);
        quiz.setQuestions(new ArrayList<>());
    }

    @Test
    public void testCreateQuiz_Success() throws Exception {
        QuizRequest request = new QuizRequest();
        request.setTitle("New Quiz");
        request.setDescription("New Description");
        request.setTimeLimit(45);
        request.setIsPublished(true);

        mockMvc.perform(post("/api/admin/quiz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAdminQuizzes() throws Exception {
        mockMvc.perform(get("/api/admin/quiz")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateQuiz_Success() throws Exception {
        QuizRequest request = new QuizRequest();
        request.setTitle("Updated Quiz");
        request.setDescription("Updated Description");
        request.setTimeLimit(50);
        request.setIsPublished(true);

        mockMvc.perform(put("/api/admin/quiz/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteQuiz_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/quiz/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetQuizResults() throws Exception {
        mockMvc.perform(get("/api/admin/quiz/1/results")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
