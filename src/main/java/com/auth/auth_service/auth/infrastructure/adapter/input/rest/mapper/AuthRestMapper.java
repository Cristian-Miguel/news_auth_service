package com.auth.auth_service.auth.infrastructure.adapter.input.rest.mapper;

import com.auth.auth_service.auth.domain.model.Authentication;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request.*;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.response.AuthResponse;
import com.auth.auth_service.user.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthRestMapper {

    User toUser(SignUpRequest signUpRequest);

    User toUser(SignInRequest signInRequest);

    User toUser(SendResetPasswordRequest sendResetPasswordRequest);

    User toUser(ResetPasswordValidateRequest resetPasswordValidateRequest);

    User toUser(VerifiedOrValidateEmailRequest verifiedEmailRequest);

    AuthResponse toAuthResponse(Authentication authentication);
}
