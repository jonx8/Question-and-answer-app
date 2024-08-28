package com.questionanswer.users.service;

import org.keycloak.representations.idm.UserRepresentation;

public interface UserService {
    UserRepresentation getUserInfo(String id);
}
