package com.auth.auth_service.auth.application.service;

import com.auth.auth_service.auth.application.port.input.SignUpUseCase;
import com.auth.auth_service.auth.application.port.input.VerifiedEmailUseCase;
import com.auth.auth_service.auth.domain.model.Authentication;
import com.auth.auth_service.role.application.port.output.RoleOutputPort;
import com.auth.auth_service.role.domain.exception.RoleNotFoundException;
import com.auth.auth_service.role.domain.model.Role;
import com.auth.auth_service.role.infrastructure.constant.RoleEnum;
import com.auth.auth_service.user.application.port.output.UserEventPublisher;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.exception.UserAlreadyExistsException;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.shared.infrastructure.utils.JwtUtils;
import com.auth.auth_service.shared.infrastructure.constant.EventType;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
public class SignUpService implements SignUpUseCase {

    private final UserOutputPort userOutputPort;
    private final RoleOutputPort roleOutputPort;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ErrorMessage errorMessage;
    private final JwtUtils jwtUtils;
    private final UserEventPublisher userEventPublisher;
    private final VerifiedEmailUseCase verifiedEmailUseCase;

    @Transactional
    @Override
    public Authentication signUp(User user) {

        validateRequest(user);

        Role role = roleOutputPort.findByEnumName(user.getRole().getEnumName())
                .orElseThrow(
                        () -> new RoleNotFoundException(errorMessage.ROLE_NOT_FOUND)
                );

        user.setUuid(UUID.randomUUID().toString());
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());
        user.setLoggerAt(LocalDateTime.now());

        user = userOutputPort.saveUser(user);

        String accessToken = jwtUtils.getToken(user);
        String refreshToken = refreshTokenService.createTokenSession(user);

        userEventPublisher.publishUserUpdatesEvent(user, EventType.USER_CREATED);

        verifiedEmailUseCase.validateEmail(user);

        return Authentication.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void validateRequest(User user)
     throws UserAlreadyExistsException {
        boolean isEmailTaken = userOutputPort.existByEmail(user.getEmail());
        boolean isUsernameTaken = userOutputPort.existByUsername(user.getUsername());

        if (isEmailTaken && isUsernameTaken){
            throw new UserAlreadyExistsException(
                    errorMessage.buildEmailAndUsernameTakenError(
                            user.getEmail(),
                            user.getUsername()
                    )
            );
        } else if (isEmailTaken) {
            throw new UserAlreadyExistsException(
                    errorMessage.buildEmailTakenError(
                            user.getEmail()
                    )
            );
        } else if (isUsernameTaken) {
            throw new UserAlreadyExistsException(
                    errorMessage.buildUsernameTakenError(
                            user.getUsername()
                    )
            );
        }
    }

}
