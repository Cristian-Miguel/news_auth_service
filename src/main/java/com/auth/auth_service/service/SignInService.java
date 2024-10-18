package com.auth.auth_service.service;

import com.auth.auth_service.dto.AuthResponse;
import com.auth.auth_service.dto.SignInRequest;

public interface SignInService {

    public AuthResponse signIn(SignInRequest request);

}
