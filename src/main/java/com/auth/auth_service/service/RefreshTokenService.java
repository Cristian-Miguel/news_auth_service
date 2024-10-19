package com.auth.auth_service.service;

import com.auth.auth_service.dto.AuthResponse;
import com.auth.auth_service.model.User;

public interface RefreshTokenService {

    public AuthResponse refreshToken(String refresh);

    public AuthResponse validateToken(String token);

    public String createTokenSession(User user);

    public String updateTokenSession(User user, String oldRefresh);

}
