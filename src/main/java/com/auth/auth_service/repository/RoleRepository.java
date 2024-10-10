package com.auth.auth_service.repository;

import com.auth.auth_service.model.Role;
import com.auth.auth_service.shared.constant.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByEnumName(RoleEnum enumName);
}
