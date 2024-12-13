package com.auth.auth_service.user.domain.event;

import com.auth.auth_service.user.infrastructure.constant.UserEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent<T> {

    private UserEventType eventType;

    private String source;

    private T payload;

}
