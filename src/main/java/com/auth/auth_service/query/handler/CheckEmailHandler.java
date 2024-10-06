package com.auth.auth_service.query.handler;

import com.auth.auth_service.query.dto.CheckEmailQuery;
import com.auth.auth_service.query.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckEmailHandler {

    private final UserQueryRepository userQueryRepository;

    @ReadOnlyProperty
    public boolean handle(CheckEmailQuery query){
        return userQueryRepository.existsByEmail(query.getEmail());
    }
}
