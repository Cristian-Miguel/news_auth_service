package com.auth.auth_service.command.dto;

import com.auth.auth_service.shared.constant.RoleEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @Email(message = "Email should be valid.")
    @NotBlank(message = "Email cannot be null.")
    private String email;

    @NotBlank(message = "Password cannot be null.")
    @Size(min=8, max = 16)
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
    private Date birthDate;

}
