package com.auth.app.controller;

import com.auth.app.dto.UserResponse;
import com.auth.app.entity.User;
import com.auth.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authenticated user endpoints.
 *
 * Protected (requires valid Bearer JWT):
 *   GET /api/users/me
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Return the profile of the currently authenticated user.
     *
     * GET /api/users/me
     *
     * Request header:
     *   Authorization: Bearer <JWT_TOKEN>
     *
     * Success response (200 OK):
     * {
     *   "id": 1,
     *   "username": "vilas",
     *   "email": "vilas@gmail.com",
     *   "phone": "9876543210"
     * }
     *
     * No password or password hash is ever returned.
     *
     * @param userDetails injected by Spring Security from the JWT filter
     * @return UserResponse DTO
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        return ResponseEntity.ok(new UserResponse(user));
    }
}
