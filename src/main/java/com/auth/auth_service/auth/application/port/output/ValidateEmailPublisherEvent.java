package com.auth.auth_service.auth.application.port.output;

import com.auth.auth_service.auth.domain.event.ValidateEmailEvent;

public interface ValidateEmailPublisherEvent {

    void publishValidateEmail(ValidateEmailEvent validateEmailEvent);

}
