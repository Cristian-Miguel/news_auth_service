package com.auth.auth_service.user.application.port.input;

public interface UserEventListener {

    void onUserUpdates(String message);
}
