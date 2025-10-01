package com.questionanswer.users.service;

import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface UserService {

    List<UserRepresentation> getUsers();

    UserRepresentation getUserById(String userId);

    List<UserRepresentation> findUserByEmail(String email);

    List<UserRepresentation> findUserByUsername(String username);

    UserRepresentation createUser(UserRepresentation user);

    UserRepresentation updateUser(String userId, UserRepresentation user);

    void deleteUser(String userId);

    void resetPassword(String userId, String newPassword);
}