package com.auth.auth_service.service.Impl;

import com.auth.auth_service.dto.AuthResponse;
import com.auth.auth_service.dto.SignInRequest;
import com.auth.auth_service.exception.BadUserCredentialsException;
import com.auth.auth_service.exception.LockedAccountException;
import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.UserRepository;
import com.auth.auth_service.service.SignInService;
import com.auth.auth_service.shared.constant.ErrorMessage;
import com.auth.auth_service.shared.constant.SystemConstant;
import com.auth.auth_service.shared.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SignInServiceImpl implements SignInService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RefreshTokenServiceImpl refreshTokenService;

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
    public AuthResponse signIn(SignInRequest request){

        User user = userRepository.findByUsername(
                request.getUsername()
        ).orElseThrow(
                () -> new BadUserCredentialsException(
                        errorMessage.BAD_CREDENTIALS
                )
        );

        if (isAccountLocked(user))
            throw new LockedAccountException(errorMessage.LOCKED_ACCOUNT);

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));

            // Reset failed attempts on successful login
            resetFailedAttempts(user);

            // Update the last logger
            user.setLoggerAt(LocalDateTime.now());
            userRepository.save(user);

            // Generate JWT token
            String refreshToken = refreshTokenService.createTokenSession(user);

            String accessToken = jwtUtils.getToken(user);

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

        userRepository.save(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailAttempts(0);
        user.setLockTime(null); // Unlock account
        userRepository.save(user);
    }

    private void unlockAccount(User user) {
        user.setFailAttempts(0);
        user.setLockTime(null); // Unlock account
        userRepository.save(user);
    }
}
