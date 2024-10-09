package com.auth.auth_service.shared.constant;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN("Administrator", "Administrator of the news app"),
    READERS("Readers", "Can view news articles, subscribe to content, and access limited features without login for public news"),
    JOURNALIST("Journalist", "Can view news articles, subscribe to content, and access limited features without login for public news"),
    PUBLISHER("Publisher", "Can review and publish articles, moderate content, and manage journalist submissions.");

    private String name;
    private String description;

    private RoleEnum( String name, String description ){
        this.name = name;
        this.description = description;
    }
}
