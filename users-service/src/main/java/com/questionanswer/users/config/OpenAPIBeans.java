package com.questionanswer.users.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.*;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIBeans {

    @Configuration
    @SecurityScheme(name = "keycloak", type = SecuritySchemeType.OAUTH2,
            flows = @OAuthFlows(
                    authorizationCode = @OAuthFlow(
                            authorizationUrl = "${keycloak.url}/realms/${keycloak.realm}/protocol/openid-connect/auth",
                            tokenUrl = "${keycloak.url}/realms/${keycloak.realm}/protocol/openid-connect/token",
                            scopes = {
                                    @OAuthScope(name = "openid"),
                                    @OAuthScope(name = "microprofile-jwt")
                            }
                    )
            )
    )
    protected static class SpringDocSecurity {
    }
}
