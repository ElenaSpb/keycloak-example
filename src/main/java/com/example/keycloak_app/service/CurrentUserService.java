package com.example.keycloak_app.service;

import com.example.keycloak_app.auth.LenasOidcUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public LenasOidcUser getCurrentOidcUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userIsLogin(auth))
            return (LenasOidcUser) auth.getPrincipal();
        throw new RuntimeException("There are no login user.");
    }

    private boolean userIsLogin(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
    }
}
