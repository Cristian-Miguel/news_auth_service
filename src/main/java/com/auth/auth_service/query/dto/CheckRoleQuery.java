package com.auth.auth_service.query.dto;

import com.auth.auth_service.shared.constant.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckRoleQuery {

    private RoleEnum roleEnum;
}
