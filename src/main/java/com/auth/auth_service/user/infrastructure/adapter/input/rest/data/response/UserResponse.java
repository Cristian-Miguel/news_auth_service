package com.auth.auth_service.user.infrastructure.adapter.input.rest.data.response;

import java.time.LocalDate;

import com.auth.auth_service.role.domain.model.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String uuid;

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private Role role;

    private LocalDate birthDate;

    private String uuidAdmin;

}
