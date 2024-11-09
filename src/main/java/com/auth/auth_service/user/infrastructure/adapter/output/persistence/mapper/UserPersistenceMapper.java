package com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper;

import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    UserEntity toUserEntity(User user);

    User toUser(UserEntity userEntity);
}
