package com.auth.auth_service.user.infrastructure.adapter.input.rest.mapper;

import org.mapstruct.Mapper;

import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.input.rest.data.request.UserRequest;
import com.auth.auth_service.user.infrastructure.adapter.input.rest.data.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    User toUser(UserRequest userRequest);

    User toUser(UserResponse userResponse);

    UserResponse toCreateUserResponse(User user);
}
