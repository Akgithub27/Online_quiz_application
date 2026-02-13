package com.online_quiz.service;

import com.online_quiz.dto.AuthRequest;
import com.online_quiz.dto.AuthResponse;
import com.online_quiz.entity.User;
import com.online_quiz.exception.UnauthorizedException;
import com.online_quiz.repository.UserRepository;
import com.online_quiz.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email already registered");
        }

        User.UserRole role;
        try {
            role = User.UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid role. Must be ADMIN or PARTICIPANT");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setIsActive(true);

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        String token = jwtProvider.generateToken(user.getEmail(), user.getRole().name());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole().name());
        response.setMessage("User registered successfully");

        return response;
    }

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtProvider.generateToken(user.getEmail(), user.getRole().name());

            log.info("User logged in successfully: {}", user.getEmail());

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setName(user.getName());
            response.setRole(user.getRole().name());
            response.setMessage("User logged in successfully");

            return response;
        } catch (BadCredentialsException e) {
            log.warn("Login failed for email: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }
    }
}
