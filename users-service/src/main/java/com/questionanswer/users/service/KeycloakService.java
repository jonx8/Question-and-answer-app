package com.questionanswer.users.service;

import com.questionanswer.users.dto.RegisterRequest;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakService {
    UserRepresentation createUser(RegisterRequest request);

    void deleteUser(String userId);

    void deleteUserWithRetry(String userId);

}

