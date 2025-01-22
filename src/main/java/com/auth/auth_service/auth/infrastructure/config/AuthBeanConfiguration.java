package com.auth.auth_service.auth.infrastructure.config;

import com.auth.auth_service.auth.application.port.input.VerifiedEmailUseCase;
import com.auth.auth_service.auth.application.port.output.ResetPasswordPublisherEvent;
import com.auth.auth_service.auth.application.port.output.TokenSessionOutputPort;
import com.auth.auth_service.auth.application.port.output.ValidateEmailPublisherEvent;
import com.auth.auth_service.auth.application.service.*;
import com.auth.auth_service.auth.infrastructure.adapter.output.eventpublisher.KafkaResetPasswordEventPublisherAdapter;
import com.auth.auth_service.auth.infrastructure.adapter.output.eventpublisher.KafkaValidateEmailEventPublisherAdapter;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.TokenSessionPersistenceAdapter;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.mapper.TokenSessionPersistenceMapper;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.repository.TokenSessionRepository;
import com.auth.auth_service.role.application.port.output.RoleOutputPort;
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.shared.infrastructure.utils.EncryptionUtil;
import com.auth.auth_service.shared.infrastructure.utils.JwtUtils;
import com.auth.auth_service.user.application.port.output.UserEventPublisher;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
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
            final JwtUtils jwtUtils,
            final UserEventPublisher userEventPublisher,
            final VerifiedEmailUseCase verifiedEmailUseCase
            ){
        return new SignUpService(
                userOutputPort,
                roleOutputPort,
                refreshTokenService,
                passwordEncoder,
                errorMessage,
                jwtUtils,
                userEventPublisher,
                verifiedEmailUseCase
        );
    }

    @Bean
    public SignInService signInService(
            final UserOutputPort userOutputPort,
            final RefreshTokenService refreshTokenService,
            final ErrorMessage errorMessage,
            final JwtUtils jwtUtils,
            final AuthenticationManager authenticationManager,
            final UserEventPublisher userEventPublisher
    ){
        return new SignInService(
                userOutputPort,
                refreshTokenService,
                errorMessage,
                jwtUtils,
                authenticationManager,
                userEventPublisher
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

    @Bean
    public ResetPasswordService resetPasswordService(
        final ErrorMessage errorMessage,
        final JwtUtils jwtUtils,
        final UserOutputPort userOutputPort,
        final ResetPasswordPublisherEvent resetPasswordEvent,
        final PasswordEncoder passwordEncoder,
        final UserEventPublisher userEventPublisher
    ) {
        return new ResetPasswordService(
                errorMessage,
                jwtUtils,
                userOutputPort,
                resetPasswordEvent,
                passwordEncoder,
                userEventPublisher
        );
    }

    @Bean
    public VerifiedEmailService verifiedEmailService(
            final ErrorMessage errorMessage,
            final UserOutputPort userOutputPort,
            final UserEventPublisher userEventPublisher,
            final JwtUtils jwtUtils,
            final ValidateEmailPublisherEvent validateEmailPublisherEvent
            ){
        return new VerifiedEmailService(
                errorMessage,
                userOutputPort,
                userEventPublisher,
                jwtUtils,
                validateEmailPublisherEvent
        );
    }

    @Bean
    public KafkaResetPasswordEventPublisherAdapter kafkaResetPasswordEventPublisherAdapter(
            final KafkaTemplate<String, String> kafkaTemplate,
            final ObjectMapper objectMapper
    ) {
        return new KafkaResetPasswordEventPublisherAdapter(
          kafkaTemplate,
          objectMapper
        );
    }

    @Bean
    public KafkaValidateEmailEventPublisherAdapter kafkaValidateEmailEventPublisherAdapter(
            final KafkaTemplate<String, String> kafkaTemplate,
            final ObjectMapper objectMapper
    ){
        return new KafkaValidateEmailEventPublisherAdapter(
                kafkaTemplate,
                objectMapper
        );
    }

}
