package com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordValidateRequest {

    @NotBlank(message = "Password cannot be null.")
    private String password;

}
