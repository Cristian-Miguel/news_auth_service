package com.auth.auth_service.auth.infrastructure.adapter.input.rest.mapper;

import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request.SignInRequest;
import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request.SignUpRequest;
import com.auth.auth_service.user.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthRestMapper {

    User toUser(SignUpRequest signUpRequest);

    User toUser(SignInRequest signInRequest);

}
