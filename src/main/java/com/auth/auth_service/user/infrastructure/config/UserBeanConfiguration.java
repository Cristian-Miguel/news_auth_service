package com.auth.auth_service.user.infrastructure.config;

import com.auth.auth_service.user.infrastructure.adapter.output.persistence.UserPersistenceAdapter;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
