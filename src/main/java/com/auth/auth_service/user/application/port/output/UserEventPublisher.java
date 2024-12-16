package com.auth.auth_service.user.application.port.output;

import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.shared.infrastructure.constant.EventType;

public interface UserEventPublisher {

    void publishUserUpdatesEvent(User user, EventType userEventType);

}
