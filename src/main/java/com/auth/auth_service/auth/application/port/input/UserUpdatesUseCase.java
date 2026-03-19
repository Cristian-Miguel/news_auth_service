package com.auth.auth_service.auth.application.port.input;

public interface UserUpdatesUseCase {

    void createUserByEvent(String message);

    void updateUserByEvent(String message);

    void deleteUserByEvent(String message);

}
