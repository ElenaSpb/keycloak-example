package com.example.keycloak_app.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
public class IndexController {

    @GetMapping(path = "/")
    public HashMap index() {
        OAuth2User user = ((OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return new HashMap() {{
            put("hello", user.getAttribute("name"));
            put("your email is", user.getAttribute("email"));
        }};
    }

    @GetMapping(path = "/unauthenticated")
    public String unauthenticatedRequests() {
        return "this is unauthenticated endpoint";
    }

    @GetMapping(path = "/cats")
    public List<String> getCats() {
        return List.of("cat1", "cat2");
    }
}
