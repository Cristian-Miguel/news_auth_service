package com.auth.auth_service.auth.application.service;

import com.auth.auth_service.auth.application.port.input.RefreshTokenUseCase;
import com.auth.auth_service.auth.application.port.output.TokenSessionOutputPort;
import com.auth.auth_service.auth.domain.model.Authentication;
import com.auth.auth_service.auth.domain.model.TokenSession;
import com.auth.auth_service.auth.domain.exception.BadUserCredentialsException;
import com.auth.auth_service.auth.domain.exception.RefreshTokenException;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.exception.UserNotFoundException;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.shared.infrastructure.adapter.output.RedisCachePort;
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.shared.infrastructure.utils.EncryptionUtil;
import com.auth.auth_service.shared.infrastructure.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final TokenSessionOutputPort tokenSessionOutputPort;
    private final UserOutputPort userOutputPort;
    private final EncryptionUtil encryptionUtil;
    private final ErrorMessage errorMessage;
    private final JwtUtils jwtUtils;
    private final RedisCachePort redisCachePort;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Transactional(noRollbackFor = {
            ExpiredJwtException.class,
            RefreshTokenException.class
        }
    )
    @Override
    public Authentication refreshToken(String refresh){
        try {
            String username = jwtUtils.getUsernameFromToken(refresh);
            String uuid = jwtUtils.getUuidFromToken(refresh);

            if (redisCachePort.hasKey(BLACKLIST_PREFIX + uuid)) {
                throw new BadUserCredentialsException("Access token is revoked/invalid.");
            }

            User user = userOutputPort.findByUsername(username)
                .orElseThrow(
                    () -> new UserNotFoundException(errorMessage.buildUsernameDontExistError(username))
                );
            
            Date expiredTokenDate =  jwtUtils.getExpiration(refresh);
            LocalDateTime expired = expiredTokenDate
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
            
            LocalDateTime nearToExpired = expired.minusDays(3);
            boolean needsRotation = nearToExpired.isBefore(LocalDateTime.now());
            
            String refreshTokenToReturn;

            if (needsRotation) {
                refreshTokenToReturn = updateTokenSession(user, refresh);
            } else {
                validateSessionIntegrity(uuid, refresh);
                refreshTokenToReturn = refresh;
            }

            String accessToken = jwtUtils.getToken(user);

            return Authentication.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshTokenToReturn)
                    .build();

        } catch (ExpiredJwtException ex) {
            handleExpiredToken(refresh);
            throw new RefreshTokenException("Token refresh expired.");
        } catch (MalformedJwtException ex){
            throw new RefreshTokenException("Token refresh invalid.");
        }

    }

    private void validateSessionIntegrity(String uuid, String currentRefreshToken) {
        TokenSession session = tokenSessionOutputPort.findBySessionId(uuid).orElseThrow(
            () -> new RefreshTokenException("Session not found")
        );

        if (session.isRevoked()) {
            throw new BadUserCredentialsException("Session is revoked.");
        }
        
        boolean isMatch = encryptionUtil.verifyRefreshToken(currentRefreshToken, session.getRefreshToken());
        if (!isMatch) {
            revokeSession(session);
            throw new BadUserCredentialsException("Token Reuse Detected! Session invalidated.");
        }
    }

    @Override
    public String updateTokenSession(User user, String oldRefresh) {
        String uuid = jwtUtils.getUuidFromToken(oldRefresh);

        if (redisCachePort.hasKey(BLACKLIST_PREFIX + uuid)) {
            throw new BadUserCredentialsException("Session revoked.");
        }

        TokenSession session = tokenSessionOutputPort.findBySessionId(uuid).orElseThrow(
            () -> new RefreshTokenException("Session not found")
        );

        if(session.isRevoked()){
            throw new BadUserCredentialsException("Session is revoked.");
        }

        String encryptedOld = encryptionUtil.encryptRefreshToken(oldRefresh);
        
        if (!session.getRefreshToken().equals(encryptedOld)) {
            revokeSession(session);
            throw new BadUserCredentialsException("Security Alert: Token Reuse Detected. Session terminated.");
        }

        Map<String, Object> refreshTokenMap = jwtUtils.getRefreshToken(uuid, user);
        String newRefresh = (String) refreshTokenMap.get("refresh");
        LocalDateTime expired = (LocalDateTime) refreshTokenMap.get("expired");

        session.setRefreshToken(encryptionUtil.encryptRefreshToken(newRefresh));
        session.setExpiredAt(expired);

        tokenSessionOutputPort.saveTokenSession(session);

        return newRefresh;
    }

    private void revokeSession(TokenSession session) {
        session.setRevoked(true);
        tokenSessionOutputPort.saveTokenSession(session);

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, session.getExpiredAt());
        long secondsToExpire = duration.getSeconds();

        if (secondsToExpire > 0) {
            redisCachePort.save(
                BLACKLIST_PREFIX + session.getSessionId(), 
                "compromised", 
                secondsToExpire, 
                TimeUnit.SECONDS
            );
        }
    }

    private void handleExpiredToken(String refresh) {
        try {
            String encrypted = encryptionUtil.encryptRefreshToken(refresh);
            tokenSessionOutputPort.findByRefreshToken(encrypted).ifPresent(session -> {
                tokenSessionOutputPort.deleteTokenSession(session);
            });
        } catch (Exception e) {
            // Log y continuar
        }
    }

    @Override
    public Authentication validateToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return new Authentication(token.substring(7), null);
        }
        return null;
    }

    @Override
    public String createTokenSession(User user) {
        String uuid = UUID.randomUUID().toString();
        Map<String, Object> refreshToken = jwtUtils.getRefreshToken(uuid, user);
        String refresh = (String) refreshToken.get("refresh");
        LocalDateTime expired = (LocalDateTime) refreshToken.get("expired");

        tokenSessionOutputPort.saveTokenSession(
            TokenSession.builder()
                    .sessionId(uuid)
                    .refreshToken(encryptionUtil.encryptRefreshToken(refresh))
                    .expiredAt(expired)
                    .createdAt(LocalDateTime.now())
                    .user(user)
                    .build()
        );
        return refresh;
    }

}
