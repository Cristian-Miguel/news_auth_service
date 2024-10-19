package com.auth.auth_service.service.Impl;

import com.auth.auth_service.dto.AuthResponse;
import com.auth.auth_service.exception.BadUserCredentialsException;
import com.auth.auth_service.exception.RefreshTokenException;
import com.auth.auth_service.exception.UserNotFoundException;
import com.auth.auth_service.model.TokenSession;
import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.TokenSessionRepository;
import com.auth.auth_service.repository.UserRepository;
import com.auth.auth_service.service.RefreshTokenService;
import com.auth.auth_service.shared.constant.ErrorMessage;
import com.auth.auth_service.shared.utils.EncryptionUtil;
import com.auth.auth_service.shared.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private final TokenSessionRepository tokenSessionRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final EncryptionUtil encryptionUtil;

    private final ErrorMessage errorMessage;

    private final JwtUtils jwtUtils;

    @Transactional(noRollbackFor = {
            ExpiredJwtException.class,
            RefreshTokenException.class
        }
    )
    @Override
    public AuthResponse refreshToken(String refresh){

        try {

            String username = jwtUtils.getUsernameFromToken(refresh);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(
                            () -> new UserNotFoundException(errorMessage.buildUsernameDontExistError(username))
                    );

            String accessToken = jwtUtils.getToken(user);

            String refreshToken = updateTokenSession(user, refresh);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (ExpiredJwtException ex) {

            TokenSession session = tokenSessionRepository.findByRefreshToken(refresh).orElseThrow(
                    () -> new RefreshTokenException("Token refresh invalid.")
            );

            tokenSessionRepository.delete(session);

            throw new RefreshTokenException("Token refresh invalid.");

        } catch (MalformedJwtException ex){
            throw new RefreshTokenException("Token refresh invalid.");
        }

    }

    @Override
    public AuthResponse validateToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return new AuthResponse(token.substring(7), null);
        }

        return null;
    }

    @Override
    public String createTokenSession(User user) {
        String uuid = UUID.randomUUID().toString();

        Map<String, Object> refreshToken = jwtUtils.getRefreshToken(uuid, user);

        String refresh = (String) refreshToken.get("refresh");
        LocalDateTime expired = (LocalDateTime) refreshToken.get("expired");

        tokenSessionRepository.save(
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

        if(uuid==null){
            throw new RefreshTokenException("Token refresh invalid.");
        }

        TokenSession session = tokenSessionRepository.findBySessionId(uuid).orElseThrow(
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

        tokenSessionRepository.save(session);

        return refresh;
    }

}
