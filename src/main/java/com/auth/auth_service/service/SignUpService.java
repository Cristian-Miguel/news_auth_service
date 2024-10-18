package com.auth.auth_service.service;

import com.auth.auth_service.dto.AuthResponse;
import com.auth.auth_service.dto.SignUpRequest;

public interface SignUpService {

    public AuthResponse signUp(SignUpRequest request);

}
