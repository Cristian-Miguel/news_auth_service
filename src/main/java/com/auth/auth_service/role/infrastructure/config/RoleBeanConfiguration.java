package com.auth.auth_service.role.infrastructure.config;

import com.auth.auth_service.role.infrastructure.adapter.output.persistance.RolePersistenceAdapter;
import com.auth.auth_service.role.infrastructure.adapter.output.persistance.mapper.RolePersistenceMapper;
import com.auth.auth_service.role.infrastructure.adapter.output.persistance.repository.RoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleBeanConfiguration {

    @Bean
    public RolePersistenceAdapter rolePersistenceAdapter(
            final RoleRepository roleRepository,
            final RolePersistenceMapper rolePersistenceMapper
    ){
        return new RolePersistenceAdapter(
                roleRepository,
                rolePersistenceMapper
        );
    }

}
