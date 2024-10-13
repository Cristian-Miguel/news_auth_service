package com.auth.auth_service.service;

import com.auth.auth_service.dto.SignInRequest;
import com.auth.auth_service.dto.SignUpRequest;
import com.auth.auth_service.exception.BadUserCredentialsException;
import com.auth.auth_service.exception.LockedAccountException;
import com.auth.auth_service.exception.RoleNotFoundException;
import com.auth.auth_service.model.Role;
import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.RoleRepository;
import com.auth.auth_service.repository.UserRepository;
import com.auth.auth_service.shared.constant.ErrorMessage;
import com.auth.auth_service.dto.AuthResponse;
import com.auth.auth_service.exception.UserAlreadyExistsException;
import com.auth.auth_service.shared.constant.SystemConstant;
import com.auth.auth_service.shared.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ErrorMessage errorMessage;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse signUp(SignUpRequest request){

        boolean isEmailTaken = userRepository.existsByEmail(request.getEmail());
        boolean isUsernameTaken = userRepository.existsByUsername(request.getUsername());

        if (isEmailTaken && isUsernameTaken){
            throw new UserAlreadyExistsException(
                    errorMessage.buildEmailAndUsernameTakenError(
                            request.getEmail(),
                            request.getUsername()
                    )
            );
        } else if (isEmailTaken) {
            throw new UserAlreadyExistsException(
                    errorMessage.buildEmailTakenError(
                            request.getEmail()
                    )
            );
        } else if (isUsernameTaken) {
            throw new UserAlreadyExistsException(
                    errorMessage.buildUsernameTakenError(
                            request.getUsername()
                    )
            );
        }

        Role role = roleRepository.findByEnumName(request.getRole())
                .orElseThrow(
                        () -> new RoleNotFoundException(errorMessage.ROLE_NOT_FOUND)
                );

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .role(role)
                .loggerAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtUtils.getToken(user))
                .build();
    }

    @Transactional(noRollbackFor = {
                BadUserCredentialsException.class,
                LockedAccountException.class,
                AuthenticationException.class
            }
    )
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
            String token = jwtUtils.getToken(user);
            return AuthResponse.builder().token(token).build();

        } catch (AuthenticationException ex){
            increaseFailedAttempts(user);
            throw new BadUserCredentialsException(errorMessage.BAD_CREDENTIALS);
        }
    }

    public AuthResponse validateToken(String token){
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return new AuthResponse(token.substring(7));
        }

        return null;
    }

    //Sub methods for the service class
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
