package com.example.keycloak_app.controller;

import com.example.keycloak_app.auth.WithMockLenasUser;
import com.example.keycloak_app.service.ProductService;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * SpringBootTest test with a MOCK WebEnvironment value.
 * With use @SpringBootTest without parameters or with webEnvironment = WebEnvironment.MOCK, no actual HTTP server is run.
 * Similar to Context test inside-server test.
 * KeyCloak server run in test container only for real SpringContext will run successfully
 * IT WORKS ONLY IF LOCAL DOCKER IS RUN
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class ProductControllerSpringBootRunKeycloakContainerTest {

    static KeycloakContainer keycloakContainer;
    static {
        keycloakContainer = new KeycloakContainer()
                .withRealmImportFile("realm-export.json");
        keycloakContainer.start();
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.lenas-realm.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/lenas-realm");
    }

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProductService productService;

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

