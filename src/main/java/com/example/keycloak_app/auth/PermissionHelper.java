package com.example.keycloak_app.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissionHelper {

    public static boolean hasPermission(String permission) {
        return ((LenasOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getAllPermissions().contains(permission);
    }
}
