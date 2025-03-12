package com.example.keycloak_app.controller;

import com.example.keycloak_app.auth.PermissionHelper;
import com.example.keycloak_app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping(path = "/products")
    public List<String> getProducts() {
        return productService.getPublicProducts();
    }

    @GetMapping(path = "/private-products")
    public ResponseEntity<String> getSecretProducts() {
        if (!PermissionHelper.hasPermission("lenasPermission1")) {
            return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(productService.getPrivateProducts().toString(), HttpStatus.OK);
    }
}