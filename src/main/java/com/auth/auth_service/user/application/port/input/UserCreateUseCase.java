package com.auth.auth_service.user.application.port.input;

import com.auth.auth_service.user.domain.model.User;

public interface UserCreateUseCase {

    void onCreateUser(String message);

    User onCreateUserByNewsAdmin(User user, String token);

}
