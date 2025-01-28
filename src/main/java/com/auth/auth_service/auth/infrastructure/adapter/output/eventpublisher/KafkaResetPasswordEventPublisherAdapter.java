package com.auth.auth_service.auth.infrastructure.adapter.output.eventpublisher;

import com.auth.auth_service.auth.application.port.output.ResetPasswordPublisherEvent;
import com.auth.auth_service.auth.domain.event.ResetPasswordEvent;
import com.auth.auth_service.shared.domain.event.FormatEventResponse;
import com.auth.auth_service.shared.infrastructure.constant.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
public class KafkaResetPasswordEventPublisherAdapter implements ResetPasswordPublisherEvent {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishResetPasswordEvent(ResetPasswordEvent resetPasswordEvent) {
        try {

            FormatEventResponse<ResetPasswordEvent> messageFormat = new FormatEventResponse<>(
                    EventType.RESET_PASSWORD,
                    "auth-service",
                    resetPasswordEvent
            );

            String jsonFormat = objectMapper.writeValueAsString(messageFormat);

            kafkaTemplate.send("reset-password", jsonFormat);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting event POJO to JSON: " + e.getMessage());
        }
    }

}
