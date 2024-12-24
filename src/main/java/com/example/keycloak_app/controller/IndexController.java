package com.example.keycloak_app.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
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
        // get a successful user login
        OAuth2User user = ((OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return new HashMap() {{
            put("hello", user.getAttribute("name"));
            put("your email is", user.getAttribute("email"));
        }};
    }

    @GetMapping(path = "/unauthenticated")
    public HashMap unauthenticatedRequests() {
        return new HashMap() {{
            put("this is ", "unauthenticated endpoint");
        }};
    }

    @GetMapping(path = "/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "/";
    }

    @GetMapping(path = "/cats")
    public List<String> getCats() {
        return List.of("cat1", "cat2");
    }
}
