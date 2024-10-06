package com.auth.auth_service.query.repository;

import com.auth.auth_service.shared.constant.RoleEnum;
import com.auth.auth_service.shared.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleQueryRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByEnumName(RoleEnum enumName);

}
