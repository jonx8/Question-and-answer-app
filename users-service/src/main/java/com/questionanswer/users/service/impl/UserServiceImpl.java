package com.questionanswer.users.service.impl;

import com.questionanswer.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${keycloak.realm}")
    private String realmName;

    private final Keycloak keycloak;

    @Override
    public UserRepresentation getUserInfo(String id) {
        UsersResource usersResource = keycloak.realm(realmName).users();
        return usersResource.get(id).toRepresentation();
    }

}
