package com.auth.auth_service.query.handler;

import com.auth.auth_service.query.dto.CheckEmailQuery;
import com.auth.auth_service.query.dto.CheckUsernameQuery;
import com.auth.auth_service.query.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckUsernameHandler {

    private final UserQueryRepository userQueryRepository;

    public boolean handle(CheckUsernameQuery query) {
        return userQueryRepository.existsByUsername(query.getUsername());
    }

}
