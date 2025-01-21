package com.auth.auth_service.auth.domain.event;

import com.auth.auth_service.role.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordEvent {

    private String token;

    private String email;

    private String username;

    private String firstname;

    private String lastname;

    private Role role;

    private LocalDateTime dateTime;
}
