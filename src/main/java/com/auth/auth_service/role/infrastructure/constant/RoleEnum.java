package com.auth.auth_service.role.infrastructure.constant;

import lombok.Getter;

@Getter
public enum RoleEnum {
    PUBLIC("Public", "Public client, doesn't have an account"),
    ADMINISTRATOR("Administrator", "Administrator of the news app"),
    PREMIUM("Premium", "User with susbscription can access all the content premium."),
    READERS("Readers", "Can view news articles, subscribe to content, and access limited features without login for public news"),
    NEWS_ENTERPRICE("News enterprice", "The administrator of the news account and his user like journalist and publisher"),
    JOURNALIST("Journalist", "Can view news articles, subscribe to content, and access limited features without login for public news"),
    PUBLISHER("Publisher", "Can review and publish articles, moderate content, and manage journalist submissions.");

    private String name;
    private String description;

    private RoleEnum( String name, String description ){
        this.name = name;
        this.description = description;
    }
}
