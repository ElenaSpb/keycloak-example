package com.example.keycloak_app.controller;

import com.example.keycloak_app.auth.WithMockLenasUser;
import com.example.keycloak_app.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Spring MockMVC example with Spring’s  WebApplicationContext.
 * Since we’re still using an inside-server strategy, no web server is deployed in this case.
 * But we can use smart annotations like @WithMockLenasUser here to make implementation more pragmatic.
 * the responses we’re verifying are still fake. There is no web server involved in this test either.
 * In any case, it’s a perfectly valid test since we’re checking our logic inside our class and response statuses.
 */
@AutoConfigureJsonTesters
@WebMvcTest(ProductController.class)
public class ProductControllerContextTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProductService productService;

    // This object will be initialized thanks to @AutoConfigureJsonTesters
    @Autowired
    private JacksonTester<String> jsonWriter;

    @Test
    @WithMockLenasUser
    void getAllProducts() throws Exception {
        given(productService.getPublicProducts()).willReturn(List.of("product1", "product2", "product3"));

        MockHttpServletResponse response = mvc
                .perform(get("/products").contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[\"product1\",\"product2\",\"product3\"]");
    }

    @Test
    @WithMockLenasUser
    void getPrivateProductsForbidden() throws Exception {

        MockHttpServletResponse response = mvc
                .perform(get("/private-products").contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockLenasUser(permissions = "lenasPermission1")
    void getPrivateProductsOk() throws Exception {
        given(productService.getPrivateProducts()).willReturn(List.of("pr1", "pr2", "pr3"));

        MockHttpServletResponse response = mvc
                .perform(get("/private-products").contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[pr1, pr2, pr3]");
    }
}

