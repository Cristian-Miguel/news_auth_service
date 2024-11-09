package com.auth.auth_service.role.application.port.output;

import com.auth.auth_service.role.domain.model.Role;
import com.auth.auth_service.role.infrastructure.constant.RoleEnum;

import java.util.Optional;

public interface RoleOutputPort {

    Optional<Role> findByEnumName(RoleEnum enumName);
}
