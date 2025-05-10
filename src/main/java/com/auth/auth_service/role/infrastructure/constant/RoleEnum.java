package com.auth.auth_service.role.infrastructure.constant;

import lombok.Getter;

@Getter
public enum RoleEnum {
    PUBLIC("ROLE_PUBLIC","Public", "Public client, doesn't have an account"),
    ADMINISTRATOR("ROLE_ADMINISTRATOR","Administrator", "Administrator of the news app"),
    PREMIUM("ROLE_PREMIUM","Premium", "User with susbscription can access all the content premium."),
    READERS("ROLE_READERS","Readers", "Can view news articles, subscribe to content, and access limited features without login for public news"),
    NEWS_ENTERPRICE("ROLE_NEWS_ENTERPRICE","News enterprice", "The administrator of the news account and his user like journalist and publisher"),
    JOURNALIST("ROLE_JOURNALIST","Journalist", "Can view news articles, subscribe to content, and access limited features without login for public news"),
    PUBLISHER("ROLE_PUBLISHER","Publisher", "Can review and publish articles, moderate content, and manage journalist submissions.");

    private String code;
    private String publicName;
    private String description;

    private RoleEnum(String code, String publicName, String description ){
        this.code = code;
        this.publicName = publicName;
        this.description = description;
    }
}
