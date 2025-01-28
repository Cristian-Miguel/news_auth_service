package com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendResetPasswordRequest {

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Must be an email.")
    private String email;

}
