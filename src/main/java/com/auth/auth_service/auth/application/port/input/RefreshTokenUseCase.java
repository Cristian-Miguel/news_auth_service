package com.auth.auth_service.auth.application.port.input;

import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.response.AuthResponse;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;

public interface RefreshTokenUseCase {

    AuthResponse refreshToken(String refresh);

    AuthResponse validateToken(String token);

    String createTokenSession(User user);

    String updateTokenSession(User user, String oldRefresh);

}
