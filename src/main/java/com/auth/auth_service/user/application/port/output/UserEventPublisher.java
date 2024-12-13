package com.auth.auth_service.user.application.port.output;

import com.auth.auth_service.user.domain.event.UserCreatedEvent;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.constant.UserEventType;

public interface UserEventPublisher {

    void publishUserUpdatesEvent(User user, UserEventType userEventType);

}
