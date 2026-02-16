package com.online_quiz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Standard API Response Wrapper
 * Ensures all API responses follow a consistent format
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Static factory methods for common responses
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(201, message, data);
    }

    public static ApiResponse<?> noContent(String message) {
        return new ApiResponse<>(204, message);
    }

    public static ApiResponse<?> badRequest(String message) {
        return new ApiResponse<>(400, message);
    }

    public static ApiResponse<?> unauthorized(String message) {
        return new ApiResponse<>(401, message);
    }

    public static ApiResponse<?> forbidden(String message) {
        return new ApiResponse<>(403, message);
    }

    public static ApiResponse<?> notFound(String message) {
        return new ApiResponse<>(404, message);
    }

    public static ApiResponse<?> conflict(String message) {
        return new ApiResponse<>(409, message);
    }

    public static ApiResponse<?> internalServerError(String message) {
        return new ApiResponse<>(500, message);
    }
}
