package com.auth.auth_service.auth.application.port.input;

import com.auth.auth_service.user.domain.model.User;

public interface ResetPasswordCaseUse {

    String sendResetPasswordByEmail(User user);

    String resetPasswordValidated(User user, String token);

}
