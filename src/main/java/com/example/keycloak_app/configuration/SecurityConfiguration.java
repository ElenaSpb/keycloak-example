package com.example.keycloak_app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults())
//                .logout((logout) -> logout
//                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
//                        .deleteCookies("JSESSIONID"))
                .build();
    }
}

