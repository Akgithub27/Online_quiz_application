package com.online_quiz;

import com.online_quiz.dto.AuthRequest;
import com.online_quiz.dto.AuthResponse;
import com.online_quiz.entity.User;
import com.online_quiz.exception.UnauthorizedException;
import com.online_quiz.repository.UserRepository;
import com.online_quiz.security.JwtProvider;
import com.online_quiz.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    public void setup() {
        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");
        authRequest.setName("Test User");
        authRequest.setRole("PARTICIPANT");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("encrypted_password");
        user.setRole(User.UserRole.PARTICIPANT);
    }

    @Test
    public void testRegisterSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encrypted_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtProvider.generateToken(anyString(), anyString())).thenReturn("jwt_token");

        AuthResponse response = authService.register(authRequest);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("jwt_token", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UnauthorizedException.class, () -> {
            authService.register(authRequest);
        });
    }

    @Test
    public void testLoginSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        when(jwtProvider.generateToken(anyString(), anyString())).thenReturn("jwt_token");

        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt_token", response.getToken());
    }
}
