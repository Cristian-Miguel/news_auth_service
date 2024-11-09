package com.auth.auth_service.auth.application.port.input;

import com.auth.auth_service.auth.infrastructure.adapter.input.rest.data.response.AuthResponse;
import com.auth.auth_service.user.domain.model.User;

public interface SignInUseCase {

    AuthResponse signIn(User user);

}
