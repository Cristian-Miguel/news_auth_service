package com.auth.auth_service.auth.infrastructure.adapter.output.eventpublisher;

import com.auth.auth_service.auth.application.port.output.ValidateEmailPublisherEvent;
import com.auth.auth_service.auth.domain.event.ValidateEmailEvent;
import com.auth.auth_service.shared.domain.event.FormatEventResponse;
import com.auth.auth_service.shared.infrastructure.constant.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
public class KafkaValidateEmailEventPublisherAdapter implements ValidateEmailPublisherEvent {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishValidateEmail(ValidateEmailEvent validateEmailEvent) {
        try {

            FormatEventResponse<ValidateEmailEvent> messageFormat = new FormatEventResponse<>(
                    EventType.VALIDATE_EMAIL,
                    "auth-service",
                    validateEmailEvent
            );

            String jsonFormat = objectMapper.writeValueAsString(messageFormat);

            kafkaTemplate.send("validate-email", jsonFormat);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting event POJO to JSON: " + e.getMessage());
        }
    }
}
