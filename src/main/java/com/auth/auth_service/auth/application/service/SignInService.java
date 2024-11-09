package com.auth.auth_service.auth.application.service;

import com.auth.auth_service.auth.application.port.input.SignInUseCase;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.response.AuthResponse;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request.SignInRequest;
import com.auth.auth_service.auth.domain.exception.BadUserCredentialsException;
import com.auth.auth_service.auth.domain.exception.LockedAccountException;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.repository.UserRepository;
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.shared.infrastructure.constant.SystemConstant;
import com.auth.auth_service.shared.infrastructure.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@AllArgsConstructor
public class SignInService implements SignInUseCase {

    private final UserOutputPort userOutputPort;
    private final RefreshTokenService refreshTokenService;
    private final ErrorMessage errorMessage;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional(noRollbackFor = {
            BadUserCredentialsException.class,
            LockedAccountException.class,
            AuthenticationException.class
        }
    )
    @Override
    public AuthResponse signIn(User user){

        User userComplete = userOutputPort.findByUsername(
                user.getUsername()
        ).orElseThrow(
                () -> new BadUserCredentialsException(
                        errorMessage.BAD_CREDENTIALS
                )
        );

        if (isAccountLocked(userComplete))
            throw new LockedAccountException(errorMessage.LOCKED_ACCOUNT);

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword()
            ));

            // Reset failed attempts on successful login
            resetFailedAttempts(userComplete);

            // Update the last logger
            user.setLoggerAt(LocalDateTime.now());
            userOutputPort.saveUser(userComplete);

            // Generate JWT token
            String refreshToken = refreshTokenService.createTokenSession(userComplete);

            String accessToken = jwtUtils.getToken(userComplete);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (AuthenticationException ex){
            increaseFailedAttempts(user);
            throw new BadUserCredentialsException(errorMessage.BAD_CREDENTIALS);
        }
    }

    private boolean isAccountLocked(User user){
        LocalDateTime lockTime = user.getLockTime();
        if (user.getFailAttempts() >= SystemConstant.MAX_FAILED_ATTEMPTS.getValue() &&
                lockTime == null
        ) {
            increaseFailedAttempts(user);
            return true;
        } else if(user.getFailAttempts() >= SystemConstant.MAX_FAILED_ATTEMPTS.getValue() &&
                lockTime.plusMinutes(SystemConstant.LOCK_DURATION_MINUTES.getValue()).isAfter(LocalDateTime.now())
        ) {
            return true;
        } else {
            unlockAccount(user);
            return false;
        }
    }

    private void increaseFailedAttempts(User user) {
        int newFailedAttempts = user.getFailAttempts() + 1;
        user.setFailAttempts(newFailedAttempts);

        if (newFailedAttempts >= SystemConstant.MAX_FAILED_ATTEMPTS.getValue()) {
            user.setLockTime(LocalDateTime.now()); // Lock account
        }

        userOutputPort.saveUser(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailAttempts(0);
        user.setLockTime(null); // Unlock account
        userOutputPort.saveUser(user);
    }

    private void unlockAccount(User user) {
        user.setFailAttempts(0);
        user.setLockTime(null); // Unlock account
        userOutputPort.saveUser(user);
    }
}
