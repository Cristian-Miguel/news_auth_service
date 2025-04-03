package com.auth.auth_service.auth.application.service;

import com.auth.auth_service.auth.application.port.input.SignOutUseCase;
import com.auth.auth_service.auth.application.port.output.TokenSessionOutputPort;
import com.auth.auth_service.auth.domain.exception.BadUserCredentialsException;
import com.auth.auth_service.auth.domain.exception.RefreshTokenException;
import com.auth.auth_service.auth.domain.model.TokenSession;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.exception.UserNotFoundException;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.shared.infrastructure.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class SignOutService implements SignOutUseCase {

    private final UserOutputPort userOutputPort;
    private final TokenSessionOutputPort tokenSessionOutputPort;
    private final ErrorMessage errorMessage;
    private final JwtUtils jwtUtils;

    @Transactional
    @Override
    public String signOut(String refreshToken)
      throws ExpiredJwtException {
        try {
            if(refreshToken == null || refreshToken.isEmpty()){
                throw new RefreshTokenException("The refresh token is invalid");
            }
            Map<String, Object> claims = jwtUtils.getAllClaims(refreshToken);
            String uuid = (String) claims.get("uuid");

            TokenSession sessionActive = tokenSessionOutputPort.findBySessionId(uuid)
                    .orElseThrow(
                            () -> new RefreshTokenException("The refresh token is invalid")
                    );

            sessionActive.setRevoked(true);

            tokenSessionOutputPort.saveTokenSession(sessionActive);

            return "Successful sign out";
        } catch (ExpiredJwtException ex) {
            throw new BadUserCredentialsException(errorMessage.TOKEN_EXPIRED);
        } catch (MalformedJwtException ex) {
            throw new BadUserCredentialsException(errorMessage.TOKEN_MALFORMAT);
        } catch (SignatureException e) {//SignatureException
            throw new BadUserCredentialsException(errorMessage.TOKEN_MALFORMAT);
        }
    }

    @Transactional
    @Override
    public String signOutAllSession(String refreshToken)
            throws ExpiredJwtException {
        try {
            String username = jwtUtils.getUsernameFromToken(refreshToken);

            User user = userOutputPort.findByUsername(username)
                    .orElseThrow(
                            () -> new UserNotFoundException("User not found")
                    );

            List<TokenSession> sessionsActive = tokenSessionOutputPort.findAllByUser(user);

            for (TokenSession sessionDevice : sessionsActive){
                sessionDevice.setRevoked(true);

                tokenSessionOutputPort.saveTokenSession(sessionDevice);
            }

            return "Successful sign out all session";

        } catch (ExpiredJwtException ex) {
            throw new BadUserCredentialsException(errorMessage.TOKEN_EXPIRED);
        } catch (MalformedJwtException ex) {
            throw new BadUserCredentialsException(errorMessage.TOKEN_MALFORMAT);
        }
    }

}
