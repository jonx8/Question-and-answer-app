package com.questionanswer.users.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakBeans {

    @Value("${keycloak.clientId}")
    private String clientId;

    @Value("${keycloak.clientSecret}")
    private String clientSecret;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.url}")
    private String serverUrl;


    @Bean
    public Keycloak keycloak(){

        return KeycloakBuilder.builder()
                .clientSecret(clientSecret)
                .clientId(clientId)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .serverUrl(serverUrl)
                .build();
    }
}
