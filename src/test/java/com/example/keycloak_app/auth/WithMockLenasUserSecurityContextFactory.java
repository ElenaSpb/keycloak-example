package com.example.keycloak_app.auth;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WithMockLenasUserSecurityContextFactory implements WithSecurityContextFactory<WithMockLenasUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockLenasUser customUser) {
        var permissions = Set.of(customUser.permissions().replaceAll(" ", "").split(","));

        var lenasOidcUser = mock(LenasOidcUser.class);
        when(lenasOidcUser.getAllPermissions()).thenReturn(permissions);

        var authentication = mock(OAuth2LoginAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(lenasOidcUser);
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}