package com.auth.auth_service.user.infrastructure.adapter.input.rest.data.request;

import com.auth.auth_service.role.infrastructure.constant.RoleEnum;
import com.auth.auth_service.user.domain.model.User;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import org.hibernate.validator.constraints.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "Email cannot be null.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Password cannot be null.")
    private String password;

    @NotBlank(message = "Username cannot be empty.")
    private String username;

    @NotBlank(message = "First name cannot be null.")
    private String firstName;

    @NotBlank(message = "Last name cannot be null.")
    private String lastName;

    @NotNull(message = "The role cannot be null.")
    private RoleEnum role;

    @NotNull(message = "Birth date cannot be null.")
    @Past(message = "Birth date should be valid")
    private LocalDate birthDate;

    @NotNull(message = "Uuid admin cannot be null.")
    @UUID(message = "Uuid admin should be valid.")
    private String uuidAdmin;

}
