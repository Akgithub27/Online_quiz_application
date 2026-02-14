package com.online_quiz;

import com.online_quiz.dto.AuthRequest;
import com.online_quiz.dto.AuthResponse;
import com.online_quiz.exception.UnauthorizedException;
import com.online_quiz.repository.UserRepository;
import com.online_quiz.security.JwtProvider;
import com.online_quiz.service.AuthService;
import com.online_quiz.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testRegister_Success() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("newuser@test.com");
        request.setPassword("password123");
        request.setName("New User");
        request.setRole("PARTICIPANT");

        when(userRepository.existsByEmail("newuser@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtProvider.generateToken("newuser@test.com", "PARTICIPANT")).thenReturn("token123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    public void testRegister_EmailAlreadyExists() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("existing@test.com");
        request.setPassword("password123");
        request.setName("Existing User");
        request.setRole("PARTICIPANT");

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRegister_InvalidRole() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("newuser@test.com");
        request.setPassword("password123");
        request.setName("New User");
        request.setRole("INVALID_ROLE");

        when(userRepository.existsByEmail("newuser@test.com")).thenReturn(false);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_Success() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        when(jwtProvider.generateToken("user@test.com", "PARTICIPANT")).thenReturn("token123");

        // Note: actual login test would need proper authentication setup
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
