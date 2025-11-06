package com.questionanswer.questions.components;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    public boolean isAdmin(JwtAuthenticationToken accessToken) {
        return accessToken.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isOwnerOrAdmin(JwtAuthenticationToken accessToken, UUID ownerId) {
        return isAdmin(accessToken) || accessToken.getName().equals(ownerId.toString());
    }

    public UUID getCurrentUserId(JwtAuthenticationToken accessToken) {
        return UUID.fromString(accessToken.getName());
    }
}