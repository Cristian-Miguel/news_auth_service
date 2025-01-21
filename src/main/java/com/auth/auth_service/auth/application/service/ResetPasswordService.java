package com.auth.auth_service.auth.application.service;

import com.auth.auth_service.auth.application.port.input.ResetPasswordCaseUse;
import com.auth.auth_service.auth.application.port.output.ResetPasswordPublisherEvent;
import com.auth.auth_service.auth.domain.event.ResetPasswordEvent;
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.shared.infrastructure.utils.JwtUtils;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.exception.UserNotFoundException;
import com.auth.auth_service.user.domain.model.User;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ResetPasswordService implements ResetPasswordCaseUse {

    private ErrorMessage errorMessage;
    private JwtUtils jwtUtils;
    private UserOutputPort userOutputPort;
    private ResetPasswordPublisherEvent resetPasswordPublisherEvent;

    @Override
    public String sendResetPasswordByEmail(User user){
        //Get the user with all the data
        User userComplete = userOutputPort.findByEmail(user.getEmail()).orElseThrow(
                () -> new UserNotFoundException(errorMessage.buildEmailDontExistError(user.getEmail()))
        );

        //get the token to upload the password
        String token = jwtUtils.getToken(userComplete);

        //publish the event
        resetPasswordPublisherEvent.publishResetPasswordEvent(
                ResetPasswordEvent.builder()
                        .token(token)
                        .email(userComplete.getEmail())
                        .username(userComplete.getUsername())
                        .firstname(userComplete.getFirstName())
                        .lastname(userComplete.getLastName())
                        .role(userComplete.getRole())
                        .dateTime(LocalDateTime.now())
                        .build()
        );

        return "Success email sending";
    }

}
