package com.miguelpina.app.models.service;

public interface ISecurityService {
    String findLoggedInUsername();

    void autoLogin(String username, String password);
}
