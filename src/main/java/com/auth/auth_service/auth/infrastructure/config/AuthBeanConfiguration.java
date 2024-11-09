package com.auth.auth_service.auth.infrastructure.config;

import com.auth.auth_service.auth.application.port.output.TokenSessionOutputPort;
import com.auth.auth_service.auth.application.service.RefreshTokenService;
import com.auth.auth_service.auth.application.service.SignInService;
import com.auth.auth_service.auth.application.service.SignOutService;
import com.auth.auth_service.auth.application.service.SignUpService;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.TokenSessionPersistenceAdapter;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.mapper.TokenSessionPersistenceMapper;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.repository.TokenSessionRepository;
import com.auth.auth_service.role.application.port.output.RoleOutputPort;
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.shared.infrastructure.utils.EncryptionUtil;
import com.auth.auth_service.shared.infrastructure.utils.JwtUtils;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthBeanConfiguration {

    @Bean
    public TokenSessionPersistenceAdapter tokenSessionPersistenceAdapter(
            final TokenSessionRepository tokenSessionRepository,
            final TokenSessionPersistenceMapper tokenSessionPersistenceMapper,
            final UserPersistenceMapper userPersistenceMapper
            ){
        return new TokenSessionPersistenceAdapter(
                tokenSessionRepository,
                tokenSessionPersistenceMapper,
                userPersistenceMapper
        );
    }

    @Bean
    public RefreshTokenService refreshTokenService(
            final TokenSessionOutputPort tokenSessionOutputPort,
            final UserOutputPort userOutputPort,
            final EncryptionUtil encryptionUtil,
            final ErrorMessage errorMessage,
            final JwtUtils jwtUtils
            ){
        return new RefreshTokenService(
                tokenSessionOutputPort,
                userOutputPort,
                encryptionUtil,
                errorMessage,
                jwtUtils
        );
    }

    @Bean
    public SignUpService signUpService(
            final UserOutputPort userOutputPort,
            final RoleOutputPort roleOutputPort,
            final RefreshTokenService refreshTokenService,
            final PasswordEncoder passwordEncoder,
            final ErrorMessage errorMessage,
            final JwtUtils jwtUtils
            ){
        return new SignUpService(
                userOutputPort,
                roleOutputPort,
                refreshTokenService,
                passwordEncoder,
                errorMessage,
                jwtUtils
        );
    }

    @Bean
    public SignInService signInService(
            final UserOutputPort userOutputPort,
            final RefreshTokenService refreshTokenService,
            final ErrorMessage errorMessage,
            final JwtUtils jwtUtils,
            final AuthenticationManager authenticationManager
    ){
        return new SignInService(
                userOutputPort,
                refreshTokenService,
                errorMessage,
                jwtUtils,
                authenticationManager
        );

    }

    @Bean
    public SignOutService signOutService(
            final UserOutputPort userOutputPort,
            final TokenSessionOutputPort tokenSessionOutputPort,
            final ErrorMessage errorMessage,
            final JwtUtils jwtUtils
    ){
        return new SignOutService(
                userOutputPort,
                tokenSessionOutputPort,
                errorMessage,
                jwtUtils
        );
    }

}
