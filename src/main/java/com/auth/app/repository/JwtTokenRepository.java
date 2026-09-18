package com.auth.app.repository;

import com.auth.app.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    // Additional queries (e.g. find by token string, revocation) can be added in Phase 4
}
