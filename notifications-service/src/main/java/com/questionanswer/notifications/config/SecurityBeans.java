package com.questionanswer.notifications.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.Stream;

@Configuration
public class SecurityBeans {
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(config -> config
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                    var jwtAuthenticationConverter = new JwtAuthenticationConverter();

                    var groupsJwtAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                    groupsJwtAuthoritiesConverter.setAuthoritiesClaimName("groups");
                    groupsJwtAuthoritiesConverter.setAuthorityPrefix("");

                    var scopesJwtAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

                    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(token ->
                            Stream.concat(
                                    scopesJwtAuthoritiesConverter.convert(token).stream(),
                                    groupsJwtAuthoritiesConverter.convert(token).stream()
                            ).toList()
                    );

                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(CsrfConfigurer::disable)
                .build();
    }
}
