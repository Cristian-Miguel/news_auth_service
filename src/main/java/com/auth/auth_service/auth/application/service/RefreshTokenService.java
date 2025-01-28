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
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.shared.infrastructure.utils.EncryptionUtil;
import com.auth.auth_service.shared.infrastructure.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final TokenSessionOutputPort tokenSessionOutputPort;
    private final UserOutputPort userOutputPort;
    private final EncryptionUtil encryptionUtil;
    private final ErrorMessage errorMessage;
    private final JwtUtils jwtUtils;

    @Transactional(noRollbackFor = {
            ExpiredJwtException.class,
            RefreshTokenException.class
        }
    )
    @Override
    public Authentication refreshToken(String refresh){

        try {

            String username = jwtUtils.getUsernameFromToken(refresh);

            User user = userOutputPort.findByUsername(username)
                    .orElseThrow(
                            () -> new UserNotFoundException(errorMessage.buildUsernameDontExistError(username))
                    );

            String accessToken = jwtUtils.getToken(user);

            String refreshToken = updateTokenSession(user, refresh);

            return Authentication.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (ExpiredJwtException ex) {

            TokenSession session = tokenSessionOutputPort.findByRefreshToken(refresh).orElseThrow(
                    () -> new RefreshTokenException("Token refresh invalid.")
            );

            throw new RefreshTokenException("Token refresh invalid.");

        } catch (MalformedJwtException ex){
            throw new RefreshTokenException("Token refresh invalid.");
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

    @Override
    public String updateTokenSession(User user, String oldRefresh)
     throws ExpiredJwtException, RefreshTokenException, BadUserCredentialsException {
        Map<String, Object> claims = jwtUtils.getAllClaims(oldRefresh);
        String uuid = (String) claims.get("uuid");

        if(uuid==null) {
            throw new RefreshTokenException("Token refresh invalid.");
        }

        TokenSession session = tokenSessionOutputPort.findBySessionId(uuid).orElseThrow(
                () -> new RefreshTokenException("Token don't have access to this service")
        );

        if(session.isRevoked()){
            throw new BadUserCredentialsException("Access token is invalid to refresh.");
        }

        Map<String, Object> refreshToken = jwtUtils.getRefreshToken(uuid, user);

        String refresh = (String) refreshToken.get("refresh");
        LocalDateTime expired = (LocalDateTime) refreshToken.get("expired");

        session.setRefreshToken(encryptionUtil.encryptRefreshToken(refresh));
        session.setExpiredAt(expired);

        tokenSessionOutputPort.saveTokenSession(session);

        return refresh;
    }

}
