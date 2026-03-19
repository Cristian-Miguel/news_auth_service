package com.auth.auth_service.user.infrastructure.adapter.output.eventpublisher;

import com.auth.auth_service.shared.domain.event.FormatEventResponse;
import com.auth.auth_service.user.application.port.output.UserEventPublisher;
import com.auth.auth_service.user.domain.event.UserCreatedEvent;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.auth.auth_service.shared.infrastructure.constant.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
public class KafkaUserEventPublisherAdapter implements UserEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserPersistenceMapper userPersistenceMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void publishUserUpdatesEvent(User user, EventType userEventType) {
        try {
            UserCreatedEvent userCreatedEvent = userPersistenceMapper.toUserCreatedEvent(user);

            FormatEventResponse<UserCreatedEvent> messageFormat = new FormatEventResponse<>(
                    userEventType,
                    "auth-service",
                    userCreatedEvent
            );

            String jsonFormat = objectMapper.writeValueAsString(messageFormat);

            kafkaTemplate.send("user-updates", jsonFormat);
            
        } catch (JsonProcessingException e) {
            System.err.println("Error converting user to JSON: " + e.getMessage());
        }
    }
}
