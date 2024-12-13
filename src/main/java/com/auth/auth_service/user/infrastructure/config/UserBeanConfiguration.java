package com.auth.auth_service.user.infrastructure.config;

import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.infrastructure.adapter.input.eventlistener.KafkaUserEventListenerAdapter;
import com.auth.auth_service.user.infrastructure.adapter.output.eventpublisher.KafkaUserEventPublisherAdapter;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.UserPersistenceAdapter;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class UserBeanConfiguration {

    @Bean
    public UserPersistenceAdapter userPersistenceAdapter(
            final UserRepository userRepository,
            final UserPersistenceMapper userPersistenceMapper
            ){
        return new UserPersistenceAdapter(
          userRepository,
          userPersistenceMapper
        );
    }

    @Bean
    public KafkaUserEventPublisherAdapter userEventPublisherAdapter(
            final KafkaTemplate<String, String> kafkaTemplate,
            final ObjectMapper objectMapper,
            final UserPersistenceMapper userPersistenceMapper
            ){
        return new KafkaUserEventPublisherAdapter(kafkaTemplate, userPersistenceMapper, objectMapper);
    }

    @Bean
    public KafkaUserEventListenerAdapter userEventListenerAdapter(
            final UserOutputPort userOutputPort,
            final ObjectMapper objectMapper,
            final UserPersistenceMapper userPersistenceMapper
    ){
        return new KafkaUserEventListenerAdapter(objectMapper, userOutputPort, userPersistenceMapper);
    }
}
