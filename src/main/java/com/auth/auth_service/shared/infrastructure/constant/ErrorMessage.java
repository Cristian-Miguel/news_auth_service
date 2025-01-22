package com.auth.auth_service.shared.infrastructure.constant;

import org.springframework.stereotype.Component;

@Component
public class ErrorMessage {

    public final String ROLE_NOT_FOUND = "The role is not in the system.";
    public final String BAD_CREDENTIALS = "Invalid username or password.";
    public final String LOCKED_ACCOUNT = "Your account is locked due to too many failed login attempts. Please try again later.";
    public final String TOKEN_EXPIRED = "Token has expired.";
    public final String TOKEN_MALFORMAT = "Token doesn't have the correct format.";
    public final String EMAIL_VALIDATED = "Email has already been validated.";

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

    public String buildUsernameDontExistError(String username){
        return "The username " +
                "'" +
                username +
                "'" +
                " isn't exist in the system.";
    }

    public String buildEmailDontExistError(String email){
        return "The email " +
                "'" +
                email +
                "'" +
                " isn't exist in the system.";
    }
}
