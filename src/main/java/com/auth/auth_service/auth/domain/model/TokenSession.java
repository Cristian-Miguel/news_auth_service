package com.auth.auth_service.auth.domain.model;

import com.auth.auth_service.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenSession {

    private Long id;

    private String sessionId;

    private String refreshToken;

    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    @Default
    private boolean isRevoked = false;

    private User user;
}
