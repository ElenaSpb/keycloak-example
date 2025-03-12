package com.example.keycloak_app.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {
    public List<String> getPublicProducts() {
        return Arrays.asList("iPad", "iPod", "iPhone");
    }

    public List<String> getPrivateProducts() {
        return Arrays.asList("Вятка", "Победа");
    }
}