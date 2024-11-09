package com.auth.auth_service.auth.infrastructure.adapter.output.persistance.repository;

import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.entity.TokenSessionEntity;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenSessionRepository extends JpaRepository<TokenSessionEntity, Long> {

    Optional<TokenSessionEntity> findByRefreshToken(String refreshToken);
    
    Optional<TokenSessionEntity> findBySessionId(String sessionId);

    List<TokenSessionEntity> findAllByUser(UserEntity user);

    boolean existsBySessionId(String sessionId);

}
