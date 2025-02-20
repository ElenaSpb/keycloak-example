package com.example.keycloak_app.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LenasOidcUser extends DefaultOidcUser {

    private final Set<String> allPermissions = new HashSet<>();

    public LenasOidcUser(OidcIdToken idToken, List<GrantedAuthority> authorities, Set<String> permissions) {
        super(authorities, idToken);
        allPermissions.addAll(permissions);
    }

    @Override
    public String getName() {
        return getIdToken().getEmail();
    }

    public Set<String> getAllPermissions() {
        return allPermissions;
    }
}
