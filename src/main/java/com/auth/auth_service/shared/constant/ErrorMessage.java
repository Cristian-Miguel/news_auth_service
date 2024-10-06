package com.auth.auth_service.shared.constant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorMessage {

    public final String ROLE_NOT_FOUND = "The role is not in the system.";

    public String buildEmailTakenError(String email){
        return "The email " +
                "'" +
                email +
                "'" +
                " is already taken in the system.";
    }

    public String buildUsernameTakenError(String username){
        return "The username " +
                "'" +
                username +
                "'" +
                " is already taken in the system.";
    }

    public String buildEmailAndUsernameTakenError(String email, String username){
        return "The username " +
                "'" +
                username +
                "'" +
                " and the email "+
                "'"+
                email+
                "'"+
                " is already taken in the system.";
    }
}
