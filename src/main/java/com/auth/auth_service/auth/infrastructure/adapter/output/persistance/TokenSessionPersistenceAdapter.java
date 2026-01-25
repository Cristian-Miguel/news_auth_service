package com.auth.auth_service.auth.infrastructure.adapter.output.persistance;

import com.auth.auth_service.auth.application.port.output.TokenSessionOutputPort;
import com.auth.auth_service.auth.domain.model.TokenSession;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.entity.TokenSessionEntity;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.mapper.TokenSessionPersistenceMapper;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.repository.TokenSessionRepository;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TokenSessionPersistenceAdapter implements TokenSessionOutputPort {

    private final TokenSessionRepository tokenSessionRepository;
    private final TokenSessionPersistenceMapper tokenSessionPersistenceMapper;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public void deleteByExpiredAtBefore(LocalDateTime now) {
        tokenSessionRepository.deleteByExpiredAtBefore(now);
    }

    @Override
    public TokenSession saveTokenSession(TokenSession tokenSession) {
        TokenSessionEntity tokenSessionEntity = tokenSessionPersistenceMapper.toTokenSessionEntity(tokenSession);
        tokenSessionEntity = tokenSessionRepository.save(tokenSessionEntity);

        return tokenSessionPersistenceMapper.toTokenSession(tokenSessionEntity);
    }

    @Override
    public Optional<TokenSession> findByRefreshToken(String refreshToken) {
        final Optional<TokenSessionEntity> tokenSessionEntity = tokenSessionRepository.findByRefreshToken(refreshToken);

        if(tokenSessionEntity.isEmpty())
            return Optional.empty();

        final TokenSession tokenSession = tokenSessionPersistenceMapper.toTokenSession(tokenSessionEntity.get());

        return Optional.of(tokenSession);
    }

    @Override
    public Optional<TokenSession> findBySessionId(String sessionId) {
        final Optional<TokenSessionEntity> tokenSessionEntity = tokenSessionRepository.findBySessionId(sessionId);

        if(tokenSessionEntity.isEmpty())
            return Optional.empty();

        final TokenSession tokenSession = tokenSessionPersistenceMapper.toTokenSession(tokenSessionEntity.get());

        return Optional.of(tokenSession);
    }

    public List<TokenSession> findAllByUser(User user) {
        UserEntity userEntity = userPersistenceMapper.toUserEntity(user);
        final List<TokenSessionEntity> tokenSessionEntityList = tokenSessionRepository.findAllByUser(userEntity);

        return tokenSessionPersistenceMapper.toListTokenSession(tokenSessionEntityList);
    }

    @Override
    public boolean existsBySessionId(String sessionId) {
        return tokenSessionRepository.existsBySessionId(sessionId);
    }

    @Override
    public TokenSession deleteTokenSession(TokenSession tokenSession) {
        TokenSessionEntity tokenSessionEntity = tokenSessionPersistenceMapper.toTokenSessionEntity(tokenSession);
        tokenSessionRepository.delete(tokenSessionEntity);
        return tokenSession;
    }
}
