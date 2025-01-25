package com.auth.auth_service.user.infrastructure.adapter.input.eventlistener;

import com.auth.auth_service.shared.domain.event.FormatEventResponse;
import com.auth.auth_service.user.application.port.input.UserCreateUseCase;
import com.auth.auth_service.user.application.port.input.UserDeleteUseCase;
import com.auth.auth_service.user.application.port.input.UserEventListener;
import com.auth.auth_service.user.application.port.input.UserUpdateUseCase;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.event.UserCreatedEvent;
import com.auth.auth_service.user.domain.event.UserUpdateEvent;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.auth.auth_service.shared.infrastructure.constant.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

@AllArgsConstructor
public class KafkaUserEventListenerAdapter implements UserEventListener {

    private final ObjectMapper objectMapper;
    private final UserCreateUseCase userCreateUseCase;
    private final UserUpdateUseCase userUpdateUseCase;
    private final UserDeleteUseCase userDeleteUseCase;

    @Override
    @KafkaListener(topics = "user-updates", groupId = "auth-group")
    public void onUserUpdates(String message) {
        try {
            FormatEventResponse eventResponse = objectMapper.readValue(message, FormatEventResponse.class);

            if(!eventResponse.getSource().equals("user-service"))
                return;

            if(eventResponse.getEventType().equals(EventType.USER_CREATED)){
                userCreateUseCase.onCreateUser(message);
            } else if(eventResponse.getEventType().equals(EventType.USER_UPDATE)) {
                userUpdateUseCase.onUpdateUser(message);
            } else if(eventResponse.getEventType().equals(EventType.USER_DELETE)) {
                userDeleteUseCase.onDeleteUser(message);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
