package com.auth.auth_service.command.handler;

import com.auth.auth_service.command.dto.SignUpRequest;
import com.auth.auth_service.command.repository.UserCommandRepository;
import com.auth.auth_service.query.dto.CheckEmailQuery;
import com.auth.auth_service.query.dto.CheckRoleQuery;
import com.auth.auth_service.query.dto.CheckUsernameQuery;
import com.auth.auth_service.query.handler.CheckEmailHandler;
import com.auth.auth_service.query.handler.CheckRoleEnumHandler;
import com.auth.auth_service.query.handler.CheckUsernameHandler;
import com.auth.auth_service.shared.constant.ErrorMessage;
import com.auth.auth_service.shared.dto.AuthResponse;
import com.auth.auth_service.shared.exception.RoleNotFoundException;
import com.auth.auth_service.shared.exception.UserAlreadyExistsException;
import com.auth.auth_service.shared.model.Role;
import com.auth.auth_service.shared.model.User;
import com.auth.auth_service.shared.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@AllArgsConstructor
public class SignUpHandler {

    private final UserCommandRepository userCommandRepository;
    private final PasswordEncoder passwordEncoder;
    private final CheckEmailHandler checkEmailHandler;
    private final CheckUsernameHandler checkUsernameHandler;
    private final CheckRoleEnumHandler checkRoleEnumHandler;
    private final ErrorMessage errorMessage;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponse singUp(SignUpRequest request){

        boolean isEmailTaken = checkEmailHandler.handle(new CheckEmailQuery(request.getEmail()));
        boolean isUsernameTaken = checkUsernameHandler.handle(new CheckUsernameQuery(request.getUsername()));

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

        Role role = checkRoleEnumHandler.handler(new CheckRoleQuery(request.getRole()));
//                .orElseThrow(
//                        () -> new RoleNotFoundException(errorMessage.ROLE_NOT_FOUND)
//                );

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .role(role)
                .loggerAt(new Date())
                .updateAt(new Date())
                .createAt(new Date())
                .build();

        userCommandRepository.save(user);

        return AuthResponse.builder()
                .token(jwtUtils.getToken(user))
                .build();
    }

}
