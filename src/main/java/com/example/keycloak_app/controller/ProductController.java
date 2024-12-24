package com.example.keycloak_app.controller;

import com.example.keycloak_app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * <DESCRIPTION>.
 *
 * @author elena_moshnikova@rntgroup.com
 */
@Controller
class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping(path = "/products")
    public List<String> getProducts() {
        return productService.getProducts();
    }
}