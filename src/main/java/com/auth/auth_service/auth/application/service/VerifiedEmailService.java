package com.auth.auth_service.auth.application.service;

import com.auth.auth_service.auth.application.port.input.VerifiedEmailUseCase;
import com.auth.auth_service.auth.application.port.output.ValidateEmailPublisherEvent;
import com.auth.auth_service.auth.domain.event.ValidateEmailEvent;
import com.auth.auth_service.auth.domain.exception.EmailWasAlreadyValidatedException;
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.shared.infrastructure.constant.EventType;
import com.auth.auth_service.shared.infrastructure.utils.JwtUtils;
import com.auth.auth_service.user.application.port.output.UserEventPublisher;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.exception.UserNotFoundException;
import com.auth.auth_service.user.domain.model.User;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
public class VerifiedEmailService implements VerifiedEmailUseCase {

    private final ErrorMessage errorMessage;
    private final UserOutputPort userOutputPort;
    private final UserEventPublisher userEventPublisher;
    private final JwtUtils jwtUtils;
    private final ValidateEmailPublisherEvent validateEmailPublisherEvent;

    @Override
    public String verifiedEmail(User user) {

        User userComplete = userOutputPort.findByEmail(user.getEmail())
                .orElseThrow(
                        () -> new UserNotFoundException(errorMessage.buildEmailDontExistError(user.getEmail()))
                );

        if(userComplete.isValidatedEmail())
            throw new EmailWasAlreadyValidatedException(errorMessage.EMAIL_VALIDATED);

        userComplete.setValidatedEmail(true);

        userComplete = userOutputPort.saveUser(userComplete);

        //publish an event to update the user in other service
        userEventPublisher.publishUserUpdatesEvent(userComplete, EventType.USER_UPDATE);

        return "Success email verified";
    }

    @Override
    public String validateEmail(User user) {

        User userComplete = userOutputPort.findByEmail(user.getEmail())
                .orElseThrow(
                        () -> new UserNotFoundException(errorMessage.buildEmailDontExistError(user.getEmail()))
                );

        if(userComplete.isValidatedEmail())
            throw new EmailWasAlreadyValidatedException(errorMessage.EMAIL_VALIDATED);

        String token = jwtUtils.getTokenToValidateEmail(user);

        validateEmailPublisherEvent.publishValidateEmail(
                ValidateEmailEvent.builder()
                        .token(token)
                        .email(userComplete.getEmail())
                        .username(userComplete.getUsername())
                        .dateTime(LocalDateTime.now())
                        .build()
        );

        return "Success email send to validate.";
    }

}
