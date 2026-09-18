package com.auth.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * JPA entity representing the 'jwt_tokens' table.
 * Stores JWT token records after each successful login.
 * One user can have multiple token records.
 */
@Entity
@Table(name = "jwt_tokens")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class JwtToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tid;

    /**
     * Many tokens can belong to one user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    /**
     * The full JWT string.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    /**
     * Token category — e.g. "ACCESS".
     */
    @Column(name = "cat", nullable = false, length = 20)
    private String category;

    /**
     * Token expiration timestamp.
     */
    @Column(nullable = false)
    private LocalDateTime expiry;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
