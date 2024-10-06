package com.auth.auth_service.query.handler;

import com.auth.auth_service.query.dto.CheckRoleQuery;
import com.auth.auth_service.query.repository.RoleQueryRepository;
import com.auth.auth_service.query.repository.UserQueryRepository;
import com.auth.auth_service.shared.constant.ErrorMessage;
import com.auth.auth_service.shared.exception.RoleNotFoundException;
import com.auth.auth_service.shared.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckRoleEnumHandler {

    private final RoleQueryRepository roleQueryRepository;
    private final ErrorMessage errorMessage;

    @ReadOnlyProperty
    public Role handler(CheckRoleQuery query) {
        return roleQueryRepository.findByEnumName(query.getRoleEnum())
                .orElseThrow(
                        () -> new RoleNotFoundException(errorMessage.ROLE_NOT_FOUND)
                );
    }

}
