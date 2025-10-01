package com.questionanswer.users.service.impl;

import com.questionanswer.users.service.UserService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${keycloak.realm}")
    private String realmName;

    private final Keycloak keycloak;

    public List<UserRepresentation> getUsers() {
        return keycloak.realm(realmName).users().list();
    }

    public UserRepresentation getUserById(String userId) {
        return keycloak.realm(realmName).users().get(userId).toRepresentation();
    }

    public List<UserRepresentation> findUserByEmail(String email) {
        return keycloak.realm(realmName).users().searchByEmail(email, true);
    }

    public List<UserRepresentation> findUserByUsername(String username) {
        return keycloak.realm(realmName).users().searchByUsername(username, true);
    }

    @Override
    public UserRepresentation createUser(UserRepresentation user) {
        try (Response response = keycloak.realm(realmName).users().create(user)) {
            if (response.getStatus() == 201) {
                String userId = extractUserIdFromLocation(response.getLocation());
                return getUserById(userId);
            } else {
                String error = response.readEntity(String.class);
                throw new RuntimeException("Failed to create user: " + error);
            }
        }
    }

    @Override
    public UserRepresentation updateUser(String userId, UserRepresentation user) {
        keycloak.realm(realmName).users().get(userId).update(user);
        return getUserById(userId);
    }

    @Override
    public void deleteUser(String userId) {
        keycloak.realm(realmName).users().get(userId).remove();
    }

    @Override
    public void resetPassword(String userId, String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);

        keycloak.realm(realmName).users().get(userId).resetPassword(credential);
    }

    private String extractUserIdFromLocation(URI location) {
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
}


