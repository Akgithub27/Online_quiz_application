package com.online_quiz.service;

import com.online_quiz.dto.AuthRequest;
import com.online_quiz.dto.AuthResponse;
import com.online_quiz.entity.User;
import com.online_quiz.exception.BadRequestException;
import com.online_quiz.exception.ConflictException;
import com.online_quiz.exception.InternalServerException;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthResponse register(AuthRequest request) {
        // Validate input
        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }
        
        String name = request.getName();
        String email = request.getEmail();
        String password = request.getPassword();
        String role = request.getRole();
        
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Name cannot be empty");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }
        
        if (!isValidEmail(email)) {
            throw new BadRequestException("Invalid email format");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new BadRequestException("Password cannot be empty");
        }
        
        if (password.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }
        
        if (role == null || role.trim().isEmpty()) {
            throw new BadRequestException("Role cannot be empty");
        }

        if (userRepository.existsByEmail(email.trim())) {
            throw new ConflictException("Email already registered");
        }

        User.UserRole userRole;
        try {
            userRole = User.UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role. Must be ADMIN or PARTICIPANT");
        }

        try {
            User user = new User();
            user.setName(name.trim());
            user.setEmail(email.trim());
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(userRole);
            user.setIsActive(true);

            user = userRepository.save(user);
            log.info("User saved to database with ID: {}", user.getId());
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
        } catch (Exception e) {
            log.error("Error during user registration - Full exception:", e);
            log.error("Exception type: {}", e.getClass().getName());
            log.error("Exception message: {}", e.getMessage());
            throw new InternalServerException("Error during registration: " + e.getMessage());
        }
    }

    public AuthResponse login(AuthRequest request) {
        // Validate input
        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }
        
        String email = request.getEmail();
        String password = request.getPassword();
        
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }
        
        if (!isValidEmail(email)) {
            throw new BadRequestException("Invalid email format");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new BadRequestException("Password cannot be empty");
        }
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email.trim(),
                            password
                    )
            );

            // Get principal - could be User entity or String (email)
            Object principal = authentication.getPrincipal();
            User user;
            
            if (principal instanceof User) {
                user = (User) principal;
            } else {
                // If principal is email string, fetch the user from repository
                user = userRepository.findByEmail(email.trim())
                        .orElseThrow(() -> new UnauthorizedException("User not found"));
            }
            
            if (!user.getIsActive()) {
                throw new UnauthorizedException("User account is inactive");
            }
            
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
            log.warn("Login failed for email: {}", email);
            throw new UnauthorizedException("Invalid email or password");
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during login: ", e);
            throw new InternalServerException("Error during login: " + e.getMessage());
        }
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }
}
