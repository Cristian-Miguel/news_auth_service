package com.auth.auth_service.user.application.port.input;

import com.auth.auth_service.user.domain.model.User;

public interface BlockUserUseCase {

    String onBlockUser(String message);
    
    String onUnblockUser(String message);
}
