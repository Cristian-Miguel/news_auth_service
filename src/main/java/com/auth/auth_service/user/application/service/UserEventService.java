package com.auth.auth_service.user.application.service;

import java.nio.file.AccessDeniedException;

import org.springframework.util.StringUtils;

import com.auth.auth_service.role.application.port.output.RoleOutputPort;
import com.auth.auth_service.role.domain.exception.RoleNotFoundException;
import com.auth.auth_service.role.domain.model.Role;
import com.auth.auth_service.role.infrastructure.constant.RoleEnum;
import com.auth.auth_service.role.infrastructure.utils.RoleUtil;
import com.auth.auth_service.shared.domain.event.FormatEventResponse;
import com.auth.auth_service.shared.infrastructure.constant.ErrorMessage;
import com.auth.auth_service.shared.infrastructure.utils.JwtUtils;
import com.auth.auth_service.user.application.port.input.BlockUserUseCase;
import com.auth.auth_service.user.application.port.input.UserCreateUseCase;
import com.auth.auth_service.user.application.port.input.UserDeleteUseCase;
import com.auth.auth_service.user.application.port.input.UserUpdateUseCase;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.event.UserBlockEvent;
import com.auth.auth_service.user.domain.event.UserChangeRoleEvent;
import com.auth.auth_service.user.domain.event.UserCreatedEvent;
import com.auth.auth_service.user.domain.event.UserDeleteEvent;
import com.auth.auth_service.user.domain.event.UserUnblockEvent;
import com.auth.auth_service.user.domain.event.UserUpdateEvent;
import com.auth.auth_service.user.domain.exception.UserAlreadyExistsException;
import com.auth.auth_service.user.domain.exception.UserNotFoundException;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserEventService implements UserCreateUseCase, UserUpdateUseCase, UserDeleteUseCase, BlockUserUseCase {

    private final ObjectMapper objectMapper;
    private final UserOutputPort userOutputPort;
    private final UserPersistenceMapper userPersistenceMapper;
    private final ErrorMessage errorMessage;
    private final RoleOutputPort roleOutputPort;
    private final JwtUtils jwtUtils;

    @Override
    public User onCreateUserByNewsAdmin(User user, String token) {
        try {

            if(userOutputPort.existByUsername(user.getUsername())){
                throw new UserAlreadyExistsException(errorMessage.buildUsernameTakenError(user.getUsername()));
            }

            if (userOutputPort.existByEmail(user.getEmail())){
                throw new UserAlreadyExistsException(errorMessage.buildUsernameTakenError(user.getEmail()));
            }

            if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                throw new AccessDeniedException("Invalid token");
            }

            Claims claims = jwtUtils.getAllClaims(token);
            String uuidUser = claims.get("uuid", String.class);

            User userAdmin = userOutputPort.findByUuid(uuidUser)
            .orElseThrow(
                () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(uuidUser))
            );

            if(!userAdmin.getRole().getEnumName().equals(RoleEnum.NEWS_ENTERPRICE) && 
                !userAdmin.getRole().getEnumName().equals(RoleEnum.ADMINISTRATOR)
            ) {
                throw new AccessDeniedException(errorMessage.buildAccessDeniedByRoleError(userAdmin.getRole().getEnumName()));
            }

            Role role = roleOutputPort.findByEnumName(user.getRole().getEnumName())
                .orElseThrow(
                        () -> new RoleNotFoundException(errorMessage.ROLE_NOT_FOUND)
                );
            
            user.setRole(role);

            User result = userOutputPort.saveUser(user);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreateUser(String message) {
        try {
            UserCreatedEvent eventPayload = (UserCreatedEvent) changeMessageEventToObject(message, UserCreatedEvent.class);

            User user = userPersistenceMapper.toUser(eventPayload);

            userOutputPort.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateUser(String message) {
        try {
            UserUpdateEvent eventPayload = (UserUpdateEvent) changeMessageEventToObject(message, UserUpdateEvent.class);

            User user = userPersistenceMapper.toUser(eventPayload);

            userOutputPort.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDeleteUser(String message) {
        try {
            UserDeleteEvent eventPayload = (UserDeleteEvent) changeMessageEventToObject(message, UserDeleteEvent.class);

            User user = userOutputPort.findByUuid(eventPayload.getUuid())
                    .orElseThrow(() -> new RuntimeException("User not found with UUID: " + eventPayload.getUuid()));

            userOutputPort.deleteUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String onBlockUser(String message) {
        try {
            UserBlockEvent eventPayload = (UserBlockEvent) changeMessageEventToObject(message, UserBlockEvent.class);

            User user = userOutputPort.findByUuid(eventPayload.getUuid())
                    .orElseThrow(() -> new RuntimeException("User not found with UUID: " + eventPayload.getUuid()));
            user.setLockTime(eventPayload.getLockTime());

            User updatedUser = userOutputPort.saveUser(user);

            return "User with UUID " + updatedUser.getUuid() + " has been blocked.";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String onUnblockUser(String message) {
        try {
            UserUnblockEvent eventPayload = (UserUnblockEvent) changeMessageEventToObject(message, UserUnblockEvent.class);

            User user = userOutputPort.findByUuid(eventPayload.getUuid())
                    .orElseThrow(() -> new RuntimeException("User not found with UUID: " + eventPayload.getUuid()));

            user.setLockTime(null);
            User updatedUser = userOutputPort.saveUser(user);
            return "User with UUID " + updatedUser.getUuid() + " has been unblocked.";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onChangeRole(String message) {
        try {
            UserChangeRoleEvent eventPayload = (UserChangeRoleEvent) changeMessageEventToObject(message, UserChangeRoleEvent.class);

            User user = userOutputPort.findByUuid(eventPayload.getUuid())
                    .orElseThrow(() -> new RuntimeException("User not found with UUID: " + eventPayload.getUuid()));

            user.setRole(eventPayload.getRole());
            userOutputPort.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object changeMessageEventToObject(String message, Class<?> clazz) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode payloadNode = rootNode.get("payload");

            if (payloadNode == null) {
                throw new RuntimeException("El mensaje Kafka no contiene 'payload'");
            }

            return objectMapper.treeToValue(payloadNode, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
