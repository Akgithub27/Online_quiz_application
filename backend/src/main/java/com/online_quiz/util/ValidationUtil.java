package com.online_quiz.util;

import com.online_quiz.exception.BadRequestException;
import org.springframework.security.core.Authentication;

/**
 * Validation utility class for common validations across the application
 */
public class ValidationUtil {

    /**
     * Validate that a value is not null
     */
    public static <T> T validateNotNull(T value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " cannot be null");
        }
        return value;
    }

    /**
     * Validate that a string is not null or empty
     */
    public static String validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " cannot be null or empty");
        }
        return value.trim();
    }

    /**
     * Validate email format
     */
    public static String validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || !email.matches(emailRegex)) {
            throw new BadRequestException("Invalid email format");
        }
        return email;
    }

    /**
     * Validate password strength
     */
    public static String validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }
        return password;
    }

    /**
     * Validate positive number
     */
    public static long validatePositiveNumber(Long value, String fieldName) {
        if (value == null || value <= 0) {
            throw new BadRequestException(fieldName + " must be a positive number");
        }
        return value;
    }

    /**
     * Validate role
     */
    public static String validateRole(String role) {
        if (role == null || (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("PARTICIPANT"))) {
            throw new BadRequestException("Role must be either ADMIN or PARTICIPANT");
        }
        return role.toUpperCase();
    }

    /**
     * Validate authentication and extract user email
     */
    public static String extractUserEmailFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User is not authenticated");
        }
        
        String email = authentication.getName();
        return validateNotEmpty(email, "Email");
    }
}
