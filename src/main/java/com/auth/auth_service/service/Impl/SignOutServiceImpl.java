package com.auth.auth_service.service.Impl;

import com.auth.auth_service.exception.BadUserCredentialsException;
import com.auth.auth_service.exception.RefreshTokenException;
import com.auth.auth_service.exception.UserNotFoundException;
import com.auth.auth_service.model.TokenSession;
import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.TokenSessionRepository;
import com.auth.auth_service.repository.UserRepository;
import com.auth.auth_service.service.SignOutService;
import com.auth.auth_service.shared.constant.ErrorMessage;
import com.auth.auth_service.shared.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class SignOutServiceImpl implements SignOutService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TokenSessionRepository tokenSessionRepository;

    private final ErrorMessage errorMessage;

    @Autowired
    private final JwtUtils jwtUtils;

    @Transactional
    @Override
    public String signOut(String oldRefresh)
      throws ExpiredJwtException {
        try {
            Map<String, Object> claims = jwtUtils.getAllClaims(oldRefresh);
            String uuid = (String) claims.get("uuid");

            TokenSession sessionActive = tokenSessionRepository.findBySessionId(uuid)
                    .orElseThrow(
                            () -> new RefreshTokenException("The refresh token is invalid")
                    );

            sessionActive.setRevoked(true);

            tokenSessionRepository.save(sessionActive);

            return "Successful sign out";
        } catch (ExpiredJwtException ex) {
            throw new BadUserCredentialsException(errorMessage.TOKEN_EXPIRED);
        } catch (MalformedJwtException ex) {
            throw new BadUserCredentialsException(errorMessage.TOKEN_MALFORMAT);
        }
    }

    @Transactional
    @Override
    public String signOutAllSessions(String oldRefresh)
            throws ExpiredJwtException {
        try {
            String username = jwtUtils.getUsernameFromToken(oldRefresh);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(
                            () -> new UserNotFoundException("User not found")
                    );

            List<TokenSession> sessionsActive = tokenSessionRepository.findAllByUser(user);

            for (TokenSession sessionDevice : sessionsActive){
                sessionDevice.setRevoked(true);

                tokenSessionRepository.save(sessionDevice);
            }

            return "Successful sign out";

        } catch (ExpiredJwtException ex) {
            throw new BadUserCredentialsException(errorMessage.TOKEN_EXPIRED);
        } catch (MalformedJwtException ex) {
            throw new BadUserCredentialsException(errorMessage.TOKEN_MALFORMAT);
        }
    }

}
