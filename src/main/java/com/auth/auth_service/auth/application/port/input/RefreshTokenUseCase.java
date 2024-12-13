package com.auth.auth_service.auth.application.port.input;

import com.auth.auth_service.auth.domain.model.Authentication;
import com.auth.auth_service.user.domain.model.User;

public interface RefreshTokenUseCase {

    Authentication refreshToken(String refresh);

    Authentication validateToken(String token);

    String createTokenSession(User user);

    String updateTokenSession(User user, String oldRefresh);

}
