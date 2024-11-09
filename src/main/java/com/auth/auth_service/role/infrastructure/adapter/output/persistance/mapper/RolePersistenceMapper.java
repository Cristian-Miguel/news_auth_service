package com.auth.auth_service.role.infrastructure.adapter.output.persistance.mapper;

import com.auth.auth_service.role.domain.model.Role;
import com.auth.auth_service.role.infrastructure.adapter.output.persistance.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RolePersistenceMapper {

    RoleEntity toRoleEntity(Role role);

    Role toRole(RoleEntity roleEntity);
}
