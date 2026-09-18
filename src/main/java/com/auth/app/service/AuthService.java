package com.auth.app.service;

import com.auth.app.dto.LoginRequest;
import com.auth.app.dto.LoginResponse;
import com.auth.app.dto.RegisterRequest;
import com.auth.app.entity.JwtToken;
import com.auth.app.entity.User;
import com.auth.app.exception.InvalidCredentialsException;
import com.auth.app.exception.UserAlreadyExistsException;
import com.auth.app.repository.JwtTokenRepository;
import com.auth.app.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Service responsible for authentication operations.
 *
 * Responsibilities:
 *  - Validate and execute user registration
 *  - Validate login credentials
 *  - Generate JWT after successful login
 *  - Persist JWT token record to jwt_tokens table
 *  - Return login response
 */
@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final JwtTokenRepository jwtTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService,
                       JwtService jwtService,
                       JwtTokenRepository jwtTokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userService        = userService;
        this.jwtService         = jwtService;
        this.jwtTokenRepository = jwtTokenRepository;
        this.passwordEncoder    = passwordEncoder;
    }

    /**
     * Register a new user.
     *
     * Checks:
     *  1. Username must not already exist
     *  2. Email must not already be registered
     *
     * On success, delegates user creation to UserService.
     *
     * @param request registration DTO
     * @return success message map
     */
    @Transactional
    public Map<String, String> register(RegisterRequest request) {
        if (userService.isUsernameTaken(request.getUsername())) {
            throw new UserAlreadyExistsException(
                    "Username '" + request.getUsername() + "' is already taken");
        }

        if (userService.isEmailTaken(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email '" + request.getEmail() + "' is already registered");
        }

        userService.createUser(request);

        return Map.of("message", "Registration successful");
    }

    /**
     * Authenticate a user and return a signed JWT.
     *
     * Steps:
     *  1. Look up user by username
     *  2. BCrypt-match the provided password against the stored hash
     *  3. Generate a JWT (includes userId and username claims)
     *  4. Persist the token record to jwt_tokens
     *  5. Return LoginResponse with token and username
     *
     * Both "user not found" and "wrong password" return the same
     * generic "Invalid credentials" message to prevent user enumeration.
     *
     * @param request login DTO
     * @return LoginResponse containing JWT
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Step 1: Find user — same error for not-found and wrong-password
        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        // Step 2: BCrypt password match
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // Step 3: Generate JWT
        String token = jwtService.generateToken(user);

        // Step 4: Persist token record
        JwtToken jwtToken = new JwtToken();
        jwtToken.setUser(user);
        jwtToken.setToken(token);
        jwtToken.setCategory("ACCESS");
        jwtToken.setExpiry(jwtService.extractExpiry(token));
        jwtTokenRepository.save(jwtToken);

        // Step 5: Return response — password never included
        return new LoginResponse("Login successful", token, user.getUsername());
    }
}
