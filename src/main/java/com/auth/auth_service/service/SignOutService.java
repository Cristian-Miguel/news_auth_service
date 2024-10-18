package com.auth.auth_service.service;

public interface SignOutService {

    public String signOut(String oldRefresh);

    public String signOutAllSessions(String oldRefresh);

}
