package com.auth.auth_service.auth.application.port.input;

public interface SignOutUseCase {

    String signOut(String refreshToken);

    String signOutAllSession(String refreshToken);

}
