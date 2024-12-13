package com.auth.auth_service.user.infrastructure.adapter.input.eventlistener;

import com.auth.auth_service.user.application.port.input.UserEventListener;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.event.UserCreatedEvent;
import com.auth.auth_service.user.domain.event.UserDeleteEvent;
import com.auth.auth_service.user.domain.event.UserEvent;
import com.auth.auth_service.user.domain.event.UserUpdateEvent;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.auth.auth_service.user.infrastructure.constant.UserEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

@AllArgsConstructor
public class KafkaUserEventListenerAdapter implements UserEventListener {

    private final ObjectMapper objectMapper;
    private final UserOutputPort userOutputPort;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    @KafkaListener(topics = "user-updates", groupId = "auth-group")
    public void onUserUpdates(String message) {
        try {
            UserEvent eventResponse = objectMapper.readValue(message, UserEvent.class);

            if(!eventResponse.getSource().equals("user-service"))
                return;

            if(eventResponse.getEventType().equals(UserEventType.USER_CREATED)){
                UserCreatedEvent userCreatedEvent = (UserCreatedEvent) eventResponse.getPayload();

                User user = userPersistenceMapper.toUser(userCreatedEvent);

                userOutputPort.saveUser(user);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
