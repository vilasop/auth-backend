package com.auth.app.controller;

import com.auth.app.dto.LoginRequest;
import com.auth.app.dto.LoginResponse;
import com.auth.app.dto.RegisterRequest;
import com.auth.app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for public authentication endpoints.
 *
 * Public (no JWT required):
 *   POST /api/auth/register
 *   POST /api/auth/login
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     *
     * POST /api/auth/register
     *
     * Request body:
     * {
     *   "username": "vilas",
     *   "password": "password123",
     *   "email": "vilas@gmail.com",
     *   "phone": "9876543210"
     * }
     *
     * Success response (201 Created):
     * {
     *   "message": "Registration successful"
     * }
     *
     * @param request validated registration DTO
     * @return 201 with success message
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequest request) {

        Map<String, String> response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate a user and return a JWT.
     *
     * POST /api/auth/login
     *
     * Request body:
     * {
     *   "username": "vilas",
     *   "password": "password123"
     * }
     *
     * Success response (200 OK):
     * {
     *   "message": "Login successful",
     *   "token": "JWT_TOKEN_HERE",
     *   "username": "vilas"
     * }
     *
     * @param request validated login DTO
     * @return 200 with JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
