package com.example.keycloak_app.controller;

import com.example.keycloak_app.auth.LenasOidcUser;
import com.example.keycloak_app.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Spring MockMVC example in Standalone Mode, without loading a Spring context.
 */
@ExtendWith(MockitoExtension.class)
public class ProductControllerUnitTest {

    private MockMvc mvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setup() {
        // We would need this line if we would not use the MockitoExtension
        // MockitoAnnotations.initMocks(this);
        // Here we can't use @AutoConfigureJsonTesters because there isn't a Spring context
        JacksonTester.initFields(this, new ObjectMapper());
        // MockMvc standalone approach
        mvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
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
    void getPrivateProductsForbidden() throws Exception {
        setUpContext("");
        MockHttpServletResponse response = mvc
                .perform(get("/private-products").contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void getPrivateProductsOk() throws Exception {
        setUpContext("lenasPermission1");

        given(productService.getPrivateProducts()).willReturn(List.of("pr1", "pr2", "pr3"));

        MockHttpServletResponse response = mvc
                .perform(get("/private-products").contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[pr1, pr2, pr3]");
    }

    private void setUpContext(String permissions) {
        var permissionsSet = Set.of(permissions.replaceAll(" ", "").split(","));
        var lenasOidcUser = mock(LenasOidcUser.class);
        when(lenasOidcUser.getAllPermissions()).thenReturn(permissionsSet);

        var authentication = mock(OAuth2LoginAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(lenasOidcUser);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}

