package com.auth.auth_service.role.infrastructure.adapter.output.persistance.repository;

import com.auth.auth_service.role.infrastructure.adapter.output.persistance.entity.RoleEntity;
import com.auth.auth_service.role.infrastructure.constant.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByEnumName(RoleEnum enumName);
}
