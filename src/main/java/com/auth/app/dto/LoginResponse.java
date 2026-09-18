package com.auth.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response body for POST /api/auth/login.
 * Never contains password or password hash.
 */
@Getter
@AllArgsConstructor
public class LoginResponse {

    private String message;
    private String token;
    private String username;
}
