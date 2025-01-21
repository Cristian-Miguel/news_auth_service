package com.auth.auth_service.auth.application.port.output;

import com.auth.auth_service.auth.domain.event.ResetPasswordEvent;

public interface ResetPasswordPublisherEvent {

    void publishResetPasswordEvent(ResetPasswordEvent resetPasswordEvent);

}
