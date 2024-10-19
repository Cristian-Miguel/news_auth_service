package com.auth.auth_service.repository;

import com.auth.auth_service.model.TokenSession;
import com.auth.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenSessionRepository extends JpaRepository<TokenSession, Long> {

    Optional<TokenSession> findByRefreshToken(String refreshToken);
    
    Optional<TokenSession> findBySessionId(String sessionId);

    List<TokenSession> findAllByUser(User user);

    boolean existsBySessionId(String sessionId);

}
