package com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper;

import com.auth.auth_service.user.domain.event.UserCreatedEvent;
import com.auth.auth_service.user.domain.event.UserDeleteEvent;
import com.auth.auth_service.user.domain.event.UserUpdateEvent;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    UserEntity toUserEntity(User user);

    User toUser(UserEntity userEntity);

    UserCreatedEvent toUserCreatedEvent(User user);

    User toUser(UserCreatedEvent userCreatedEvent);

    UserUpdateEvent toUserUpdateEvent(User user);

    User toUser(UserUpdateEvent userUpdateEvent);

    UserDeleteEvent toUserDeleteEvent(User user);

    User toUser(UserDeleteEvent userDeleteEvent);
}
