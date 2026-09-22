package com.auth.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration.
 *
 * Circular-dependency fix:
 *   UserDetailsService is no longer a @Bean inside this class.
 *   It is now a standalone @Component (CustomUserDetailsService) that
 *   SecurityConfig and JwtAuthenticationFilter both depend on independently:
 *
 *     CustomUserDetailsService  ←──  JwtAuthenticationFilter
 *                               ←──  SecurityConfig
 *
 *   No cycle exists because CustomUserDetailsService does not depend on
 *   either SecurityConfig or JwtAuthenticationFilter.
 *
 * Session policy : STATELESS (JWT-based — no server-side sessions)
 * CSRF           : disabled (REST API)
 *
 * Public endpoints:
 *   POST /api/auth/register
 *   POST /api/auth/login
 *
 * Protected endpoints:
 *   GET  /api/users/me  (requires valid Bearer JWT)
 *   All other endpoints are protected by default.
 *
 * CORS:
 *   Allows requests from http://localhost:5173 (Vite dev server).
 *   Update allowed origins in Phase 3 to match the deployed frontend URL.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthFilter           = jwtAuthFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    // ── Security filter chain ─────────────────────────────────────────────────
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── BCrypt password encoder ───────────────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── DaoAuthenticationProvider ─────────────────────────────────────────────
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ── AuthenticationManager ─────────────────────────────────────────────────
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── CORS — Phase 2 development configuration ──────────────────────────────
    // Allows the React Vite dev server to reach the backend during Phase 3 integration.
    // Update allowedOrigins to the deployed frontend URL in production.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
    "http://localhost:5173",
    "https://YOUR-ACTUAL-VERCEL-URL.vercel.app"
));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
