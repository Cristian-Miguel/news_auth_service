package com.auth.auth_service.auth.application.port.output;

import com.auth.auth_service.auth.domain.model.TokenSession;
import com.auth.auth_service.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenSessionOutputPort {

    TokenSession saveTokenSession(TokenSession tokenSession);

    Optional<TokenSession> findByRefreshToken(String refreshToken);

    Optional<TokenSession> findBySessionId(String sessionId);

    List<TokenSession> findAllByUser(User user);

    boolean existsBySessionId(String sessionId);

    TokenSession deleteTokenSession(TokenSession tokenSession);

    void deleteByExpiredAtBefore(LocalDateTime now);
}
