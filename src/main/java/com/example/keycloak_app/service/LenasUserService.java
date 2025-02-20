package com.example.keycloak_app.service;

import com.example.keycloak_app.auth.LenasOidcUser;
import com.nimbusds.jwt.JWTParser;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LenasUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private Map<String, Set<String>> rolePermissionMap =
            Map.of("role1", Set.of("permission1"),
                    "lenasRole", Set.of("lenasPermission1", "lenasPermission2"),
                    "lenasGroupRole", Set.of("lenasPermission1", "lenasPermission3")
            );

    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {
        OidcIdToken oidcIdToken = oidcUserRequest.getIdToken();
        String email = oidcIdToken.getEmail();
        // User user = userRepository.findFirst(email);
        // create user in application repo if it is absent
        OAuth2AccessToken accessToken = oidcUserRequest.getAccessToken();
        List<String> roles = getRoles(accessToken);
        List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        Set<String> permissions = getAllPermissions(roles);
        return new LenasOidcUser(oidcUserRequest.getIdToken(), authorities, permissions);
    }

    private List<String> getRoles(OAuth2AccessToken token) {
        try {
            var realmAccess = (Map) JWTParser.parse(token.getTokenValue())
                    .getJWTClaimsSet()
                    .getClaim("realm_access");
            ArrayList<String> rolesNode = (ArrayList<String>) realmAccess.get("roles");
            return rolesNode.stream().map(Object::toString).collect(Collectors.toList());
        } catch (ParseException e) {
            throw new AuthenticationServiceException("Error while obtaining user keycloak roles.");
        }
    }

    Set<String> getAllPermissions(List<String> roles) {
        // some business logic getting permissions for roles from DB
        return roles.stream()
                .filter(role -> rolePermissionMap.containsKey(role))
                .map(role -> rolePermissionMap.get(role))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
