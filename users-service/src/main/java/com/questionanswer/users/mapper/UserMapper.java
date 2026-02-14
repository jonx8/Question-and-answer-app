package com.questionanswer.users.mapper;

import com.questionanswer.users.dto.RegisterRequest;
import com.questionanswer.users.dto.UserProfileResponse;
import com.questionanswer.users.entity.UserProfile;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Component
public class UserMapper {
    public UserRepresentation toUserRepresentation(RegisterRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(true);
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));

        return user;
    }


    public UserProfileResponse toResponse(UserProfile userProfile) {
        return new UserProfileResponse(
                userProfile.getId(),
                userProfile.getUsername(),
                userProfile.getEmail(),
                userProfile.getFirstName(),
                userProfile.getLastName(),
                userProfile.getCreatedAt()
        );
    }

    public UserProfile toEntity(UserRepresentation userRepresentation) {
        return UserProfile.builder()
                .id(UUID.fromString(userRepresentation.getId()))
                .username(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .createdAt(Instant.now())
                .build();
    }
}