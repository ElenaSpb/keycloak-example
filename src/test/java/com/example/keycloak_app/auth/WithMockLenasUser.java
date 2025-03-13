package com.example.keycloak_app.auth;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockLenasUserSecurityContextFactory.class)
public @interface WithMockLenasUser {
    String name() default "Test User";
    String permissions() default "some";
}