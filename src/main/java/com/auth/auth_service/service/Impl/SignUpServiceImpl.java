package com.auth.auth_service.service.Impl;

import com.auth.auth_service.dto.AuthResponse;
import com.auth.auth_service.dto.SignUpRequest;
import com.auth.auth_service.exception.RoleNotFoundException;
import com.auth.auth_service.exception.UserAlreadyExistsException;
import com.auth.auth_service.model.Role;
import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.RoleRepository;
import com.auth.auth_service.repository.UserRepository;
import com.auth.auth_service.service.SignUpService;
import com.auth.auth_service.shared.constant.ErrorMessage;
import com.auth.auth_service.shared.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final RefreshTokenServiceImpl refreshTokenService;

    private final PasswordEncoder passwordEncoder;
    private final ErrorMessage errorMessage;
    private final JwtUtils jwtUtils;

    @Transactional
    @Override
    public AuthResponse signUp(SignUpRequest request){

        validateRequest(request);

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

        String accessToken = jwtUtils.getToken(user);
        String refreshToken = refreshTokenService.createTokenSession(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void validateRequest(SignUpRequest request)
     throws UserAlreadyExistsException {
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
    }

}
