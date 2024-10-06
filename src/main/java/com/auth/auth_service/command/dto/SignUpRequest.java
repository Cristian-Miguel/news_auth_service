package com.auth.auth_service.command.dto;

import com.auth.auth_service.shared.constant.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotBlank(message = "Username cannot be null.")
    private String username;

    @NotBlank(message = "First name cannot be null.")
    private String firstName;

    @NotBlank(message = "Last name cannot be null.")
    private String lastName;

    @NotBlank(message = "Role cannot be null.")
    private RoleEnum role;

    @NotBlank(message = "Birth data cannot be null.")
    @Past(message = "Birth date should be valid")
    private Date birthDate;

}
