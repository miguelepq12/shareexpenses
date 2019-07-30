package com.miguelpina.app.auth.service;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;

public interface ISecurityService {
    String autoLogin(String username, String password) throws AuthenticationException, IOException;
}
