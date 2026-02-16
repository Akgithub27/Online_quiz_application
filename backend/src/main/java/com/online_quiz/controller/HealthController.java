package com.online_quiz.controller;
import com.online_quiz.repository.UserRepository;import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * Provides endpoints for monitoring application health and status
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check endpoints")
@Slf4j
public class HealthController {

    private final UserRepository userRepository;

    /**
     * Simple health check endpoint
     * Returns 200 OK if application is running
     */
    @GetMapping("/health")
    @Operation(summary = "Simple health check", description = "Returns OK if application is running")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * Detailed health check endpoint
     * Returns detailed information about application status
     */
    @GetMapping("/health/detailed")
    @Operation(summary = "Detailed health check", description = "Returns detailed application health information")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> healthInfo = new HashMap<>();
            healthInfo.put("status", "UP");
            healthInfo.put("timestamp", LocalDateTime.now());
            healthInfo.put("application", "Online Quiz Application");
            healthInfo.put("version", "1.0.0");
            healthInfo.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());
            
            // Memory info
            Map<String, Object> memory = new HashMap<>();
            Runtime runtime = Runtime.getRuntime();
            memory.put("totalMemoryMB", runtime.totalMemory() / (1024 * 1024));
            memory.put("usedMemoryMB", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024));
            memory.put("maxMemoryMB", runtime.maxMemory() / (1024 * 1024));
            healthInfo.put("memory", memory);
            
            response.put("health", healthInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in health check", e);
            Map<String, Object> healthInfo = new HashMap<>();
            healthInfo.put("status", "DEGRADED");
            healthInfo.put("error", e.getMessage());
            response.put("health", healthInfo);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Ready probe for Kubernetes/container orchestration
     * Indicates if application is ready to receive traffic
     */
    @GetMapping("/ready")
    @Operation(summary = "Readiness probe", description = "Indicates if application is ready for traffic")
    public ResponseEntity<Map<String, String>> ready() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "READY");
        return ResponseEntity.ok(response);
    }

    /**
     * Liveness probe for Kubernetes/container orchestration
     * Indicates if application is alive
     */
    @GetMapping("/alive")
    @Operation(summary = "Liveness probe", description = "Indicates if application is alive")
    public ResponseEntity<Map<String, String>> alive() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ALIVE");
        return ResponseEntity.ok(response);
    }

    /**
     * Database connectivity check
     * Tests if the application can connect to and query the database
     */
    @GetMapping("/db-check")
    @Operation(summary = "Database connectivity check", description = "Tests database connection and basic query")
    public ResponseEntity<Map<String, Object>> dbCheck() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long userCount = userRepository.count();
            response.put("status", "CONNECTED");
            response.put("message", "Database connection successful");
            response.put("userCount", userCount);
            response.put("timestamp", LocalDateTime.now());
            log.info("Database check successful. Current user count: {}", userCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Database connection failed: {}", e.getMessage(), e);
            response.put("status", "FAILED");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(503).body(response);
        }
    }
}
