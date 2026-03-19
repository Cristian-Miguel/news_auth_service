package com.auth.auth_service.user.domain.event;

import com.auth.auth_service.role.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChangeRoleEvent {

    private String uuid;

    private Role role;
}
