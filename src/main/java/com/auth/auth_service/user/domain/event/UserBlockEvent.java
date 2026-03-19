package com.auth.auth_service.user.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBlockEvent {

    private String uuid;
    
    private LocalDateTime lockTime;

}
