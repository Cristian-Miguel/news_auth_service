package com.auth.auth_service.auth.application.port.input;

import com.auth.auth_service.user.domain.model.User;

public interface VerifiedEmailUseCase {

    String verifiedEmail(User user);

    String validateEmail(User user);

}
