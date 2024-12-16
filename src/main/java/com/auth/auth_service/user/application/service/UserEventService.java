package com.auth.auth_service.user.application.service;

import com.auth.auth_service.shared.domain.event.FormatEventResponse;
import com.auth.auth_service.user.application.port.input.UserCreateUseCase;
import com.auth.auth_service.user.application.port.input.UserDeleteUseCase;
import com.auth.auth_service.user.application.port.input.UserUpdateUseCase;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.event.UserCreatedEvent;
import com.auth.auth_service.user.domain.event.UserDeleteEvent;
import com.auth.auth_service.user.domain.event.UserUpdateEvent;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserEventService implements UserCreateUseCase, UserUpdateUseCase, UserDeleteUseCase {

    private final ObjectMapper objectMapper;
    private final UserOutputPort userOutputPort;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public void onCreateUser(String message) {
        try {
            FormatEventResponse<UserCreatedEvent> eventResponse = objectMapper.readValue(
                    message,
                    new TypeReference<FormatEventResponse<UserCreatedEvent>>() {}
            );

            User user = userPersistenceMapper.toUser(eventResponse.getPayload());

            userOutputPort.saveUser(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateUser(String message) {
        try {
            FormatEventResponse<UserUpdateEvent> eventResponse = objectMapper.readValue(
                    message,
                    new TypeReference<FormatEventResponse<UserUpdateEvent>>() {}
            );

            User user = userPersistenceMapper.toUser(eventResponse.getPayload());

            userOutputPort.saveUser(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDeleteUser(String message) {
        try {
            FormatEventResponse<UserDeleteEvent> eventResponse = objectMapper.readValue(
                    message,
                    new TypeReference<FormatEventResponse<UserDeleteEvent>>() {}
            );

            User user = userPersistenceMapper.toUser(eventResponse.getPayload());

            userOutputPort.deleteUser(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
