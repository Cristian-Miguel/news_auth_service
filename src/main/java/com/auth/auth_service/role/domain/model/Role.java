package com.auth.auth_service.role.domain.model;

import com.auth.auth_service.role.infrastructure.constant.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    private Long id;

    private RoleEnum enumName;

    private String name;

    private String description;

}
