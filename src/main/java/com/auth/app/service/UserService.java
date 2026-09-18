package com.auth.app.service;

import com.auth.app.dto.RegisterRequest;
import com.auth.app.entity.User;
import com.auth.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for user data operations.
 *
 * Responsibilities:
 *  - Create and persist a new user (with BCrypt-hashed password)
 *  - Look up users by username or ID
 *  - Check username / email availability
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Persist a new user to the database.
     * Hashes the plain-text password with BCrypt before saving.
     *
     * @param request registration request DTO
     * @return the saved User entity (password is the BCrypt hash)
     */
    @Transactional
    public User createUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone().trim());
        // Hash the password — plaintext is never stored
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Find a user by their username.
     *
     * @param username the username to search for
     * @return User entity, or empty Optional
     */
    public java.util.Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find a user by their primary key.
     *
     * @param id user ID
     * @return User entity, or empty Optional
     */
    public java.util.Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Check if the username is already taken.
     *
     * @param username the username to check
     * @return true if taken
     */
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username.trim());
    }

    /**
     * Check if the email address is already registered.
     *
     * @param email the email to check
     * @return true if taken
     */
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }
}
