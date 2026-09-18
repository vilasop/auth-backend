package com.auth.app.security;

import com.auth.app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Custom UserDetailsService implementation.
 *
 * Extracted into its own @Component to break the circular dependency that
 * would occur if UserDetailsService were defined as a @Bean inside SecurityConfig:
 *
 *   BEFORE (cycle):
 *     JwtAuthenticationFilter → UserDetailsService (defined in SecurityConfig)
 *     SecurityConfig → JwtAuthenticationFilter
 *     → CYCLE
 *
 *   AFTER (clean):
 *     JwtAuthenticationFilter → CustomUserDetailsService (@Component, standalone)
 *     SecurityConfig          → JwtAuthenticationFilter + CustomUserDetailsService
 *     → NO CYCLE
 *
 * Spring instantiates CustomUserDetailsService independently of SecurityConfig,
 * so all dependencies resolve without circularity.
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.auth.app.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())   // BCrypt hash — never plaintext
                .roles("USER")
                .build();
    }
}
