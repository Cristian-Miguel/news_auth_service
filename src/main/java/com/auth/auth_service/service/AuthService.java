package com.auth.auth_service.service;

import com.auth.auth_service.dto.SignUpRequest;
import com.auth.auth_service.exception.RoleNotFoundException;
import com.auth.auth_service.model.Role;
import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.RoleRepository;
import com.auth.auth_service.repository.UserRepository;
import com.auth.auth_service.shared.constant.ErrorMessage;
import com.auth.auth_service.dto.AuthResponse;
import com.auth.auth_service.exception.UserAlreadyExistsException;
import com.auth.auth_service.shared.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ErrorMessage errorMessage;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponse singUp(SignUpRequest request){

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

}
