package com.auth.auth_service.user.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteEvent {
    private Long id;

    private String uuid;

    private String email;

    private String username;
}
