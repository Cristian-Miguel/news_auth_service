package com.auth.auth_service.auth.infrastructure.adapter.input.eventlistener;

import com.auth.auth_service.auth.application.port.input.UserUpdatesUseCase;
import com.auth.auth_service.shared.domain.event.FormatEventResponse;
import com.auth.auth_service.shared.infrastructure.constant.EventType;
import com.auth.auth_service.user.application.port.input.UserEventListener;
import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.application.service.UserEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;

@AllArgsConstructor
@Slf4j
public class KafkaUserEventListenerAdapter implements UserEventListener {

    private final ObjectMapper objectMapper;
    private final UserEventService userEventService;

    @Override
    @KafkaListener(topics = "user-updates", groupId = "user-group")
    public void onUserUpdates(String message) {
        try {

            FormatEventResponse eventResponse = objectMapper.readValue(message, FormatEventResponse.class);

            if(eventResponse.getSource().equals("user-service"))
                return;

            if(eventResponse.getEventType().equals(EventType.USER_CREATED)){
                userEventService.onCreateUser(message);
            } 
            else if(eventResponse.getEventType().equals(EventType.USER_UPDATE)){
                userEventService.onUpdateUser(message);
            } 
            else if(eventResponse.getEventType().equals(EventType.USER_DELETE)){
                userEventService.onDeleteUser(message);
            }
            else if(eventResponse.getEventType().equals(EventType.USER_BLOCK)){
                userEventService.onBlockUser(message);
            }
            else if(eventResponse.getEventType().equals(EventType.USER_UNBLOCK)){
                userEventService.onUnblockUser(message);
            }
            else if(eventResponse.getEventType().equals(EventType.USER_CHANGE_ROLE)){
                userEventService.onChangeRole(message);
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializando mensaje Kafka: {}", message, e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Error inesperado procesando mensaje: {}", message, e);
            throw new RuntimeException(e);
        }
    }

}
