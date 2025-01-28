package com.auth.auth_service.auth.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateEmailEvent {

    private String token;

    private String email;

    private String username;

    private LocalDateTime dateTime;

}
