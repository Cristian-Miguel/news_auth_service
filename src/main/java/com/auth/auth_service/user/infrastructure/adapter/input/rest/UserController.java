package com.auth.auth_service.user.infrastructure.adapter.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.auth_service.shared.infrastructure.adapter.input.rest.data.response.GenericResponse;
import com.auth.auth_service.user.application.port.input.UserCreateUseCase;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.input.rest.data.request.UserRequest;
import com.auth.auth_service.user.infrastructure.adapter.input.rest.data.response.UserResponse;
import com.auth.auth_service.user.infrastructure.adapter.input.rest.mapper.UserRestMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserController {

    private final UserCreateUseCase createUserUseCase;

    private final  UserRestMapper userRestMapper;

    @PostMapping
    public ResponseEntity<GenericResponse<UserResponse>> postMethodName(@RequestHeader("Authorization") String token, @Valid @RequestBody UserRequest userRequest) {
        User user = userRestMapper.toUser(userRequest);
        user = createUserUseCase.onCreateUserByNewsAdmin(user, token);
        UserResponse userResponse = userRestMapper.toCreateUserResponse(user);
        
        return ResponseEntity.ok(
                GenericResponse.<UserResponse>builder()
                    .success(true)
                    .message("Entity created successfully")
                    .data(userResponse)
                    .build()
        );
    }

}
