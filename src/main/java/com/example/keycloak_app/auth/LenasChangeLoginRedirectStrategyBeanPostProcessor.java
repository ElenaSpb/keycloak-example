package com.example.keycloak_app.auth;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

@Component
public class LenasChangeLoginRedirectStrategyBeanPostProcessor implements BeanPostProcessor {

    /**
     * set private final redirect strategy to use custom 401 http status instead of 302
     * to solve FE issue about impossible cross-domain redirect with 302 http status.
     * due to https://github.com/spring-projects/spring-security/pull/11387
     * set redirectStrategy in SpringSecurity can be done after Spring 5.8
     * todo: delete this class after update Spring and setting IntraDefaultRedirectStrategy in SecurityConfig
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass() == DelegatingAuthenticationEntryPoint.class) {
            Field entryPoints = null;
            try {
                entryPoints = DelegatingAuthenticationEntryPoint.class.getDeclaredField("entryPoints");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            entryPoints.setAccessible(true);
            LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> map = null;
            try {
                map = (LinkedHashMap<RequestMatcher, AuthenticationEntryPoint>) entryPoints.get(bean);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            var loginUrlAuthenticationEntryPoint = map.values().stream().findFirst().get();
            LenasDefaultRedirectStrategy lenasDefaultRedirectStrategy = new LenasDefaultRedirectStrategy();
            try {
                setRedirectStrategy(loginUrlAuthenticationEntryPoint, lenasDefaultRedirectStrategy);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private void setRedirectStrategy(Object object, Object redirectStrategy) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField("redirectStrategy");
        field.setAccessible(true);
        field.set(object, redirectStrategy);
    }
}
