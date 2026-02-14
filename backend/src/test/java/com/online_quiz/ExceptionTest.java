package com.online_quiz;

import com.online_quiz.exception.ResourceNotFoundException;
import com.online_quiz.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {

    @Test
    public void testResourceNotFoundException_WithMessage() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testResourceNotFoundException_WithMessageAndCause() {
        String message = "Resource not found";
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException exception = new ResourceNotFoundException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testResourceNotFoundException_IsRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testUnauthorizedException_WithMessage() {
        String message = "Unauthorized access";
        UnauthorizedException exception = new UnauthorizedException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testUnauthorizedException_WithMessageAndCause() {
        String message = "Unauthorized access";
        Throwable cause = new RuntimeException("Root cause");
        UnauthorizedException exception = new UnauthorizedException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testUnauthorizedException_IsRuntimeException() {
        UnauthorizedException exception = new UnauthorizedException("Test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testResourceNotFoundException_CanBeCaught() {
        try {
            throw new ResourceNotFoundException("Test resource not found");
        } catch (ResourceNotFoundException e) {
            assertEquals("Test resource not found", e.getMessage());
        }
    }

    @Test
    public void testUnauthorizedException_CanBeCaught() {
        try {
            throw new UnauthorizedException("Test unauthorized");
        } catch (UnauthorizedException e) {
            assertEquals("Test unauthorized", e.getMessage());
        }
    }

    @Test
    public void testExceptionInheritance() {
        ResourceNotFoundException rnfe = new ResourceNotFoundException("Test");
        assertThrows(RuntimeException.class, () -> {
            throw rnfe;
        });

        UnauthorizedException ue = new UnauthorizedException("Test");
        assertThrows(RuntimeException.class, () -> {
            throw ue;
        });
    }
}
