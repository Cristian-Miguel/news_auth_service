package com.auth.auth_service.role.infrastructure.adapter.output.persistance;

import com.auth.auth_service.role.application.port.output.RoleOutputPort;
import com.auth.auth_service.role.domain.model.Role;
import com.auth.auth_service.role.infrastructure.adapter.output.persistance.entity.RoleEntity;
import com.auth.auth_service.role.infrastructure.adapter.output.persistance.mapper.RolePersistenceMapper;
import com.auth.auth_service.role.infrastructure.adapter.output.persistance.repository.RoleRepository;
import com.auth.auth_service.role.infrastructure.constant.RoleEnum;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
//@AllArgsConstructor
//@Component
public class RolePersistenceAdapter implements RoleOutputPort {

    private final RoleRepository roleRepository;
    private final RolePersistenceMapper rolePersistenceMapper;

    @Override
    public Optional<Role> findByEnumName(RoleEnum enumName) {
        final Optional<RoleEntity> roleEntity = roleRepository.findByEnumName(enumName);

        if(roleEntity.isEmpty())
            return Optional.empty();

        final Role role =  rolePersistenceMapper.toRole(roleEntity.get());

        return Optional.of(role);
    }

    @Override
    public Role saveUser(Role role) {
        RoleEntity roleEntity = rolePersistenceMapper.toRoleEntity(role);
        roleEntity = roleRepository.save(roleEntity);

        return rolePersistenceMapper.toRole(roleEntity);
    }
}
