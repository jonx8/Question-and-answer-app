package com.questionanswer.users.service.impl;

import com.questionanswer.users.dto.RegisterRequest;
import com.questionanswer.users.exception.KeycloakUserAlreadyExistsException;
import com.questionanswer.users.exception.KeycloakUserCreationError;
import com.questionanswer.users.exception.KeycloakUserDeleteException;
import com.questionanswer.users.mapper.UserMapper;
import com.questionanswer.users.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {
    @Value("${keycloak.realm}")
    private String realmName;

    private final Keycloak keycloak;
    private final UserMapper userMapper;


    @Override
    public UserRepresentation createUser(RegisterRequest request) {
        UserRepresentation userRepresentation = userMapper.toUserRepresentation(request);
        UsersResource usersResource = keycloak.realm(realmName).users();
        String keycloakUserId;
        try (var response = usersResource.create(userRepresentation)) {
            String error = response.readEntity(String.class);
            switch (response.getStatus()) {
                case 201 -> {
                    keycloakUserId = extractUserIdFromLocation(response.getLocation());
                    userRepresentation.setId(keycloakUserId);
                }
                case 409 -> {
                    log.error("Conflict while register keycloak user: {}", error);
                    throw new KeycloakUserAlreadyExistsException("User already exists");
                }
                default -> throw new KeycloakUserCreationError("Error while keycloak user creating: " + error);
            }
        }
        log.info("User created in Keycloak with ID: {}", keycloakUserId);

        return userRepresentation;
    }

    @Override
    public void deleteUser(String userId) {
        if (userId == null || userId.isBlank()) {
            log.error("Cannot delete user with null or blank ID");
            throw new KeycloakUserDeleteException("User ID cannot be null or blank");
        }

        try (var response = keycloak.realm(realmName).users().delete(userId)) {
            if (response.getStatus() != 204) {
                String error = response.readEntity(String.class);
                log.error("Failed delete user with id {}: {}", userId, error);
                throw new KeycloakUserDeleteException("Error while keycloak user deletion: " + error);
            }
        }
    }

    @Async
    @Retryable(
            retryFor = KeycloakUserDeleteException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void deleteUserWithRetry(String userId) {
        log.info("Attempt to delete keycloak user {}", userId);
        deleteUser(userId);
        log.info("Keycloak user {} successfully deleted", userId);
    }

    @Recover
    public void deleteUserRecover(KeycloakUserDeleteException e, String userId) {
        log.error("All attempts to delete keycloak user {} failed", userId, e);
    }


    private String extractUserIdFromLocation(java.net.URI location) {
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

}
