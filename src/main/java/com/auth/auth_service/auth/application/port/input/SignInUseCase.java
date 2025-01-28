package com.auth.auth_service.auth.application.port.input;

import com.auth.auth_service.auth.domain.model.Authentication;
import com.auth.auth_service.user.domain.model.User;

public interface SignInUseCase {

    Authentication signIn(User user);

}
